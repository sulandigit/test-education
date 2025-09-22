#!/usr/bin/env python3
"""
领课教育监控系统钉钉通知服务
处理 AlertManager 发送的告警并转发到钉钉群
"""

import json
import logging
import os
import time
import traceback
from datetime import datetime
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import quote

import requests

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 钉钉机器人配置
DINGTALK_WEBHOOK_URL = os.getenv('DINGTALK_WEBHOOK_URL', 'https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN')
DINGTALK_SECRET = os.getenv('DINGTALK_SECRET', '')

class DingTalkNotifier:
    """钉钉通知发送器"""
    
    def __init__(self, webhook_url, secret=''):
        self.webhook_url = webhook_url
        self.secret = secret
    
    def send_message(self, title, content, alert_level='INFO'):
        """发送消息到钉钉群"""
        try:
            # 根据告警级别选择颜色
            color_map = {
                'CRITICAL': '🔴',
                'WARNING': '🟡', 
                'INFO': '🔵'
            }
            
            color = color_map.get(alert_level, '🔵')
            
            message = {
                "msgtype": "markdown",
                "markdown": {
                    "title": f"{color} {title}",
                    "text": f"## {color} {title}\n\n{content}\n\n> 发送时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n> 来源: 领课教育监控系统"
                }
            }
            
            response = requests.post(
                self.webhook_url,
                json=message,
                headers={'Content-Type': 'application/json'},
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get('errcode') == 0:
                    logger.info(f"钉钉消息发送成功: {title}")
                    return True
                else:
                    logger.error(f"钉钉消息发送失败: {result.get('errmsg')}")
            else:
                logger.error(f"钉钉消息发送失败，状态码: {response.status_code}")
                
        except Exception as e:
            logger.error(f"发送钉钉消息异常: {str(e)}")
            traceback.print_exc()
        
        return False

class AlertHandler(BaseHTTPRequestHandler):
    """告警处理器"""
    
    def __init__(self, *args, **kwargs):
        self.dingtalk = DingTalkNotifier(DINGTALK_WEBHOOK_URL, DINGTALK_SECRET)
        super().__init__(*args, **kwargs)
    
    def do_POST(self):
        """处理 POST 请求"""
        try:
            # 读取请求体
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            
            # 解析 AlertManager 数据
            alert_data = json.loads(post_data.decode('utf-8'))
            
            # 处理告警
            self.process_alerts(alert_data)
            
            # 返回成功响应
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            self.wfile.write(b'{"status": "success"}')
            
        except Exception as e:
            logger.error(f"处理告警请求异常: {str(e)}")
            traceback.print_exc()
            
            # 返回错误响应
            self.send_response(500)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            self.wfile.write(b'{"status": "error"}')
    
    def process_alerts(self, alert_data):
        """处理告警数据"""
        try:
            alerts = alert_data.get('alerts', [])
            group_key = alert_data.get('groupKey', '')
            
            for alert in alerts:
                self.send_alert_notification(alert)
                
        except Exception as e:
            logger.error(f"处理告警数据异常: {str(e)}")
    
    def send_alert_notification(self, alert):
        """发送单个告警通知"""
        try:
            # 提取告警信息
            labels = alert.get('labels', {})
            annotations = alert.get('annotations', {})
            status = alert.get('status', 'unknown')
            
            alertname = labels.get('alertname', '未知告警')
            severity = labels.get('severity', 'info').upper()
            instance = labels.get('instance', '未知实例')
            team = labels.get('team', '运维团队')
            
            summary = annotations.get('summary', '无摘要信息')
            description = annotations.get('description', '无详细描述')
            
            starts_at = alert.get('startsAt', '')
            ends_at = alert.get('endsAt', '')
            
            # 格式化时间
            start_time = self.format_time(starts_at)
            end_time = self.format_time(ends_at) if ends_at else '持续中'
            
            # 构建消息内容
            if status == 'firing':
                title = f"🚨 {severity} 级别告警触发"
                content = f"""
**告警名称:** {alertname}

**告警级别:** {severity}

**受影响实例:** {instance}

**负责团队:** {team}

**告警摘要:** {summary}

**告警详情:** {description}

**开始时间:** {start_time}

**当前状态:** 🔥 告警中

---

请相关人员及时处理！
"""
            elif status == 'resolved':
                title = f"✅ {severity} 级别告警已恢复"
                content = f"""
**告警名称:** {alertname}

**告警级别:** {severity}

**受影响实例:** {instance}

**负责团队:** {team}

**告警摘要:** {summary}

**开始时间:** {start_time}

**恢复时间:** {end_time}

**当前状态:** ✅ 已恢复

---

告警已自动恢复，感谢处理！
"""
            else:
                return
            
            # 发送钉钉通知
            self.dingtalk.send_message(title, content, severity)
            
        except Exception as e:
            logger.error(f"发送告警通知异常: {str(e)}")
    
    def format_time(self, time_str):
        """格式化时间字符串"""
        try:
            if not time_str:
                return '未知时间'
            
            # 解析 ISO 格式时间
            dt = datetime.fromisoformat(time_str.replace('Z', '+00:00'))
            # 转换为本地时间显示
            return dt.strftime('%Y-%m-%d %H:%M:%S')
            
        except Exception:
            return time_str
    
    def do_GET(self):
        """处理 GET 请求（健康检查）"""
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        
        health_status = {
            "status": "healthy",
            "service": "dingtalk-webhook",
            "timestamp": datetime.now().isoformat(),
            "version": "1.0.0"
        }
        
        self.wfile.write(json.dumps(health_status).encode('utf-8'))
    
    def log_message(self, format, *args):
        """重写日志方法，使用标准日志格式"""
        logger.info(f"{self.address_string()} - {format % args}")

def run_server(port=8080):
    """启动 HTTP 服务器"""
    server_address = ('', port)
    httpd = HTTPServer(server_address, AlertHandler)
    
    logger.info(f"钉钉通知服务启动，监听端口: {port}")
    logger.info(f"健康检查端点: http://localhost:{port}/")
    logger.info(f"告警接收端点: http://localhost:{port}/webhook/critical")
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logger.info("收到中断信号，正在关闭服务...")
        httpd.shutdown()

if __name__ == '__main__':
    # 从环境变量获取端口
    port = int(os.getenv('PORT', 8080))
    run_server(port)
package com.roncoo.education.course.dao.impl;

import com.roncoo.education.common.core.base.Page;
import com.roncoo.education.common.core.base.PageUtil;
import com.roncoo.education.common.core.tools.IdWorker;
import com.roncoo.education.course.dao.UserCourseCommentReportDao;
import com.roncoo.education.course.dao.impl.mapper.UserCourseCommentReportMapper;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReport;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentReportExample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 课程评论举报 服务实现类
 *
 * @author assistant
 * @date 2025-09-20
 */
@Repository
@RequiredArgsConstructor
public class UserCourseCommentReportDaoImpl implements UserCourseCommentReportDao {

    @NotNull
    private final UserCourseCommentReportMapper mapper;

    @Override
    public int save(UserCourseCommentReport record) {
        if (record.getId() == null) {
            record.setId(IdWorker.getId());
        }
        return this.mapper.insertSelective(record);
    }

    @Override
    public int deleteById(Long id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateById(UserCourseCommentReport record) {
        record.setGmtCreate(null);
        record.setGmtModified(null);
        return this.mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public UserCourseCommentReport getById(Long id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    @Override
    public UserCourseCommentReport getByUserIdAndCommentId(Long userId, Long commentId) {
        return this.mapper.selectByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public Page<UserCourseCommentReport> page(int pageCurrent, int pageSize, Integer handleStatus) {
        UserCourseCommentReportExample example = new UserCourseCommentReportExample();
        UserCourseCommentReportExample.Criteria criteria = example.createCriteria();
        if (handleStatus != null) {
            criteria.andHandleStatusEqualTo(handleStatus);
        }
        example.setOrderByClause("id desc");
        return PageUtil.transform(this.mapper.selectByExample(example), pageCurrent, pageSize);
    }

    @Override
    public int countReportsByCommentId(Long commentId) {
        UserCourseCommentReportExample example = new UserCourseCommentReportExample();
        example.createCriteria().andCommentIdEqualTo(commentId).andStatusIdEqualTo(1);
        return this.mapper.countByExample(example);
    }
}
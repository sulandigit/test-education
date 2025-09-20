package com.roncoo.education.course.dao.impl;

import com.roncoo.education.common.core.tools.IdWorker;
import com.roncoo.education.course.dao.UserCourseCommentLikeDao;
import com.roncoo.education.course.dao.impl.mapper.UserCourseCommentLikeMapper;
import com.roncoo.education.course.dao.impl.mapper.entity.UserCourseCommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 课程评论点赞 服务实现类
 *
 * @author assistant
 * @date 2025-09-20
 */
@Repository
@RequiredArgsConstructor
public class UserCourseCommentLikeDaoImpl implements UserCourseCommentLikeDao {

    @NotNull
    private final UserCourseCommentLikeMapper mapper;

    @Override
    public int save(UserCourseCommentLike record) {
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
    public int updateById(UserCourseCommentLike record) {
        record.setGmtCreate(null);
        record.setGmtModified(null);
        return this.mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public UserCourseCommentLike getById(Long id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    @Override
    public UserCourseCommentLike getByUserIdAndCommentId(Long userId, Long commentId) {
        return this.mapper.selectByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public int countLikesByCommentId(Long commentId) {
        return this.mapper.countLikesByCommentId(commentId);
    }

    @Override
    public List<UserCourseCommentLike> listLikeCountsByCommentIds(List<Long> commentIds) {
        return this.mapper.listLikeCountsByCommentIds(commentIds);
    }

    @Override
    public List<UserCourseCommentLike> listUserLikesByCommentIds(Long userId, List<Long> commentIds) {
        return this.mapper.listUserLikesByCommentIds(userId, commentIds);
    }

    @Override
    public int deleteByUserIdAndCommentId(Long userId, Long commentId) {
        return this.mapper.deleteByUserIdAndCommentId(userId, commentId);
    }
}
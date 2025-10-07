package com.liang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liang.blog.entity.TodoItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TodoItemMapper extends BaseMapper<TodoItem> {

    @Select(
            """
            SELECT category_id AS categoryId, COUNT(*) AS entryCount
            FROM todo_items
            WHERE user_id = #{userId}
              AND scope = #{scope}
              AND is_deleted = 0
            GROUP BY category_id
            """)
    List<CategoryEntryCount> countByCategory(
            @Param("userId") Long userId, @Param("scope") String scope);

    class CategoryEntryCount {
        private Long categoryId;
        private Long entryCount;

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getEntryCount() {
            return entryCount;
        }

        public void setEntryCount(Long entryCount) {
            this.entryCount = entryCount;
        }
    }
}

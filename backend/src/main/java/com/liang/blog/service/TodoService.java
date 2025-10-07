package com.liang.blog.service;

import com.liang.blog.dto.todo.TodoBoardResponse;
import com.liang.blog.dto.todo.TodoBoardUpsertRequest;
import com.liang.blog.dto.todo.TodoCategorySummaryResponse;
import com.liang.blog.dto.todo.TodoEntryResponse;
import com.liang.blog.dto.todo.TodoEntryUpsertRequest;
import com.liang.blog.entity.enums.TodoScope;
import java.util.List;

public interface TodoService {

    List<TodoCategorySummaryResponse> listCategorySummary(Long userId, TodoScope scope);

    List<TodoEntryResponse> listEntries(Long userId, TodoScope scope, Long categoryId);

    TodoEntryResponse createEntry(Long userId, TodoScope scope, TodoEntryUpsertRequest request);

    TodoEntryResponse updateEntry(Long userId, TodoScope scope, Long entryId, TodoEntryUpsertRequest request);

    TodoBoardResponse getBoard(Long userId, TodoScope scope);

    TodoBoardResponse saveBoard(Long userId, TodoScope scope, TodoBoardUpsertRequest request);
}

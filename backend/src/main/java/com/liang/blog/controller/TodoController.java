package com.liang.blog.controller;

import com.liang.blog.dto.common.ApiResponse;
import com.liang.blog.dto.todo.TodoBoardResponse;
import com.liang.blog.dto.todo.TodoBoardUpsertRequest;
import com.liang.blog.dto.todo.TodoCategorySummaryResponse;
import com.liang.blog.dto.todo.TodoEntryResponse;
import com.liang.blog.dto.todo.TodoEntryUpsertRequest;
import com.liang.blog.entity.enums.TodoScope;
import com.liang.blog.security.AuthenticatedUser;
import com.liang.blog.security.CurrentUser;
import com.liang.blog.service.TodoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@Validated
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/{scope}/categories")
    public ApiResponse<List<TodoCategorySummaryResponse>> listCategories(
            @CurrentUser AuthenticatedUser currentUser, @PathVariable("scope") String scope) {
        TodoScope todoScope = TodoScope.fromString(scope);
        List<TodoCategorySummaryResponse> responses =
                todoService.listCategorySummary(currentUser.getId(), todoScope);
        return ApiResponse.success(responses);
    }

    @GetMapping("/{scope}/entries")
    public ApiResponse<List<TodoEntryResponse>> listEntries(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable("scope") String scope,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        TodoScope todoScope = TodoScope.fromString(scope);
        List<TodoEntryResponse> responses =
                todoService.listEntries(currentUser.getId(), todoScope, categoryId);
        return ApiResponse.success(responses);
    }

    @PostMapping("/{scope}/entries")
    public ApiResponse<TodoEntryResponse> createEntry(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable("scope") String scope,
            @Valid @RequestBody TodoEntryUpsertRequest request) {
        TodoScope todoScope = TodoScope.fromString(scope);
        TodoEntryResponse response = todoService.createEntry(currentUser.getId(), todoScope, request);
        return ApiResponse.success("创建成功", response);
    }

    @PutMapping("/{scope}/entries/{id}")
    public ApiResponse<TodoEntryResponse> updateEntry(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable("scope") String scope,
            @PathVariable("id") Long entryId,
            @Valid @RequestBody TodoEntryUpsertRequest request) {
        TodoScope todoScope = TodoScope.fromString(scope);
        TodoEntryResponse response = todoService.updateEntry(currentUser.getId(), todoScope, entryId, request);
        return ApiResponse.success("更新成功", response);
    }

    @GetMapping("/{scope}")
    public ApiResponse<TodoBoardResponse> getBoard(
            @CurrentUser AuthenticatedUser currentUser, @PathVariable("scope") String scope) {
        TodoScope todoScope = TodoScope.fromString(scope);
        TodoBoardResponse response = todoService.getBoard(currentUser.getId(), todoScope);
        return ApiResponse.success(response);
    }

    @PutMapping("/{scope}")
    public ApiResponse<TodoBoardResponse> saveBoard(
            @CurrentUser AuthenticatedUser currentUser,
            @PathVariable("scope") String scope,
            @Valid @RequestBody TodoBoardUpsertRequest request) {
        TodoScope todoScope = TodoScope.fromString(scope);
        TodoBoardResponse response = todoService.saveBoard(currentUser.getId(), todoScope, request);
        return ApiResponse.success("保存成功", response);
    }
}

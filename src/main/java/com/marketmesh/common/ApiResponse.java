package com.marketmesh.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic API response wrapper that can be used to standardize responses across the application.
 * It contains a success flag, a message, and optional data of type T.
 *
 * @param <T> The type of the data included in the response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // Indicates whether the API call was successful or not
    private boolean success;
    private String message;
    private T data;
   

    // Convenience static methods so you don't write "new ApiResponse<>(...)" everywhere
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, null);
    }

    // Add this alongside the existing success() and error() methods
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
package com.mongault.kiku.ui.auth;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mongault.kiku.data.repository.AuthRepository;
import com.mongault.kiku.data.repository.RepositoryCallback;
import com.mongault.kiku.model.AuthResponse;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResponse> authSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel(Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public void login(String email, String password) {
        if (!validateFieldsLogin(email, password)) return;

        isLoading.setValue(true);
        authRepository.login(email, password, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                isLoading.setValue(false);
                authSuccess.setValue(data);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                error.setValue(errorMessage);
            }
        });
    }

    public void register(String username, String email, String password, String confirmPassword) {
        if (!validateFields(username, email, password, confirmPassword)) return;

        isLoading.setValue(true);
        authRepository.register(username, email, password, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse data) {
                isLoading.setValue(false);
                authSuccess.setValue(data);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
                error.setValue(errorMessage);
            }
        });
    }

    private boolean validateFields(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || username.isEmpty()) {
            error.setValue("Username is required");
            return false;
        }
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email and password are required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setValue("Invalid email address");
            return false;
        }
        if (password.length() < 6) {
            error.setValue("Password must be at least 6 characters");
            return false;
        }
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            error.setValue("Passwords do not match");
            return false;
        }
        return true;
    }

    private boolean validateFieldsLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email and password are required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setValue("Invalid email address");
            return false;
        }
        if (password.length() < 6) {
            error.setValue("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    public LiveData<AuthResponse> getAuthSuccess() { return authSuccess; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}
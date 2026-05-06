package com.mongault.kiku.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.mongault.kiku.R;
import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ViewToolbarBinding;
import com.mongault.kiku.ui.auth.LoginActivity;

public class ToolbarManager {

    private final AppCompatActivity activity;
    private final ViewToolbarBinding binding;

    public ToolbarManager(AppCompatActivity activity, Toolbar toolbar) {
        this.activity = activity;
        this.binding = ViewToolbarBinding.bind(toolbar);
        setup();
    }

    private void setup() {
        activity.setSupportActionBar(binding.toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.textUsername.setOnClickListener(v -> showPopupMenu());
    }

    public void setUsername(String username) {
        binding.textUsername.setText(username);
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(activity, binding.textUsername);
        popup.inflate(R.menu.menu_user);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menuProfile) {
                navigateToProfile();
                return true;
            } else if (id == R.id.menuLogout) {
                logout();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void navigateToProfile() {
        // TODO activity.startActivity(new Intent(activity, ProfileActivity.class));
    }

    private void logout() {
        TokenManager.getInstance(activity).clearToken();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
package com.example.projectexpensetrackeradmin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectexpensetrackeradmin.R;
import com.example.projectexpensetrackeradmin.model.Project;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * ProjectAdapter
 * Binds project data to RecyclerView items
 * and delegates edit, delete, and navigation actions to the Activity.
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectActionListener {
        void onEditProject(Project project);
        void onDeleteProject(Project project, int position);
        void onOpenProject(Project project);
    }

    private final Context context;
    private final List<Project> projectList;
    private final OnProjectActionListener listener;

    public ProjectAdapter(Context context, List<Project> projectList, OnProjectActionListener listener) {
        this.context = context;
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.tvProjectName.setText(project.getProjectName());
        holder.tvProjectCode.setText("Code: " + project.getProjectCode());
        holder.tvProjectManager.setText("Manager: " + project.getProjectManager());
        holder.tvProjectStatus.setText("Status: " + project.getProjectStatus());
        holder.tvProjectBudget.setText(
                "Budget: " + String.format(Locale.getDefault(), "%,.0f VND", project.getProjectBudget())
        );

        bindProjectImage(holder.imgProjectPreview, project.getImagePath());

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onDeleteProject(projectList.get(currentPosition), currentPosition);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onEditProject(projectList.get(currentPosition));
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onOpenProject(projectList.get(currentPosition));
            }
        });
    }

    private void bindProjectImage(ImageView imageView, String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName, tvProjectCode, tvProjectManager, tvProjectStatus, tvProjectBudget;
        ImageView imgProjectPreview;
        Button btnEdit, btnDelete;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProjectPreview = itemView.findViewById(R.id.imgProjectPreview);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectCode = itemView.findViewById(R.id.tvProjectCode);
            tvProjectManager = itemView.findViewById(R.id.tvProjectManager);
            tvProjectStatus = itemView.findViewById(R.id.tvProjectStatus);
            tvProjectBudget = itemView.findViewById(R.id.tvProjectBudget);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
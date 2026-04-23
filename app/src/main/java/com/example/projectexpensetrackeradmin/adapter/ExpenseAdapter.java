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
import com.example.projectexpensetrackeradmin.model.Expense;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * ExpenseAdapter
 * Binds expense data to RecyclerView items
 * and delegates edit and delete actions to the Activity.
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnExpenseActionListener {
        void onEditExpense(Expense expense);
        void onDeleteExpense(Expense expense, int position);
    }

    private final Context context;
    private final List<Expense> expenseList;
    private final OnExpenseActionListener listener;

    public ExpenseAdapter(Context context, List<Expense> expenseList,
                          OnExpenseActionListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvExpenseId.setText(expense.getExpenseId());
        holder.tvAmount.setText(
                "Amount: " + String.format(
                        Locale.getDefault(),
                        "%,.0f %s",
                        expense.getAmount(),
                        expense.getCurrency()
                )
        );
        holder.tvDate.setText("Date: " + expense.getDateOfExpense());
        holder.tvType.setText("Type: " + expense.getExpenseType());

        bindExpenseImage(holder.imgExpensePreview, expense.getImagePath());

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onDeleteExpense(expenseList.get(currentPosition), currentPosition);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onEditExpense(expenseList.get(currentPosition));
            }
        });
    }

    private void bindExpenseImage(ImageView imageView, String imagePath) {
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
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseId, tvAmount, tvDate, tvType;
        ImageView imgExpensePreview;
        Button btnEdit, btnDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgExpensePreview = itemView.findViewById(R.id.imgExpensePreview);
            tvExpenseId = itemView.findViewById(R.id.tvExpenseId);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            btnEdit = itemView.findViewById(R.id.btnEditExpense);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
        }
    }
}
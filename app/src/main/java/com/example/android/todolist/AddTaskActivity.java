/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.todolist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.databinding.repacked.treelayout.internal.util.Contract;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;


public class AddTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;
    public static final String TASK_ID = "TARGET_TASK_NEED_TO_UPDATED";
    public static final int loaderID = 50;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Intent intent = getIntent();
        if (intent != null) {
            //update data
            int taskID = intent.getIntExtra(TASK_ID, -1);
            if (taskID != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt(TASK_ID, taskID);
                getSupportLoaderManager().initLoader(loaderID,bundle,this);
            }
        } else {
            // Initialize to highest mPriority by default (mPriority = 1)
            ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
            mPriority = 1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            //update data
            int taskID = intent.getIntExtra(TASK_ID, -1);
            if (taskID != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt(TASK_ID, taskID);
                getSupportLoaderManager().restartLoader(loaderID,bundle,this);
            }

    }
    }

    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {
        EditText editTextDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        String text = editTextDescription.getText().toString();
        if (text.trim().length() <= 0) {
            Toast.makeText(this, "You should like description for this task", Toast.LENGTH_SHORT).show();
        }
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, text);
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, "Inserted successfully with uri " + uri.toString(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor data = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(data!=null)
                    deliverResult(data);
                else
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                int id = bundle.getInt(TASK_ID);
                Uri taskUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                ContentResolver contentResolver = AddTaskActivity.this.getContentResolver();
                return contentResolver.query(taskUri, null, null, null, TaskContract.TaskEntry.COLUMN_PRIORITY);
            }

            @Override
            public void deliverResult(Cursor data) {
                this.data = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String description = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION));
            int priority = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY));
            if (priority == 1) {
                ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
            }
            if (priority == 2) {
                ((RadioButton) findViewById(R.id.radButton2)).setChecked(true);
            }
            if (priority == 3) {
                ((RadioButton) findViewById(R.id.radButton3)).setChecked(true);
            }
            EditText editTectDescription = (EditText) findViewById(R.id.editTextTaskDescription);
            editTectDescription.setText(description);
            mPriority = 1;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

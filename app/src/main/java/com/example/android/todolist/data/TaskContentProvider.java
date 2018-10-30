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

package com.example.android.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.regex.Matcher;

public class TaskContentProvider extends ContentProvider {

    TaskDbHelper dbHelper;
    private static final int TASKS = 100;
    private static final int TASKS_WITH_ID = 101;
    UriMatcher sMatcher = BuildMatcher();

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    private static UriMatcher BuildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(TaskContract.AUTHORITY, TaskContract.TASK_PATH, TASKS);
        matcher.addURI(TaskContract.AUTHORITY, TaskContract.TASK_PATH + "/#", TASKS_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TaskDbHelper(getContext());
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        int match = sMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (match) {
            case TASKS:
                long id = database.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                if (id > 0)
                {
                    Uri insertedUri=ContentUris.withAppendedId(uri, id);;
                    getContext().getContentResolver().notifyChange(insertedUri,null);
                    return uri;
                } else {
                    Toast.makeText(getContext(), "Error onSaving Data On Database", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet supported");
        }
        return null;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int match = sMatcher.match(uri);
        Cursor query=null;
        switch (match)
        {
            case TASKS:
                query = writableDatabase.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TASKS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String Selection= TaskContract.TaskEntry._ID+"?";
                String []SelectionArgs=new String[]{id};
                query = writableDatabase.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
        }
        query.setNotificationUri(getContext().getContentResolver(),uri);
       return query;
       // throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}

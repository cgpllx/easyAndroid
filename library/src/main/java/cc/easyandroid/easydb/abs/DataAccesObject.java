/*
 * Copyright 2015 Antonio López Marín <tonilopezmr.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.easyandroid.easydb.abs;

import android.database.Cursor;

import java.util.ArrayList;

import cc.easyandroid.easydb.core.EasyDbObject;


public interface DataAccesObject<T extends EasyDbObject> {
    void insert(T dto) throws Exception;

    void insertAll(ArrayList<T> arrayList) throws Exception;

    T findById(String id) throws Exception;

    boolean delete(String id) throws Exception;

    boolean deleteAll() throws Exception;

    ArrayList<T> findAllFromTabName(String orderBy) throws Exception;

    Cursor findAllCursor(String orderBy);


}
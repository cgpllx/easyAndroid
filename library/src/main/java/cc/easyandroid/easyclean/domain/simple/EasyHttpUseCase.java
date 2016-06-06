/*
 * Copyright 2016, The Android Open Source Project
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

package cc.easyandroid.easyclean.domain.simple;

import java.lang.reflect.Type;

import cc.easyandroid.easyclean.UseCase;
import cc.easyandroid.easyclean.repository.EasyHttpRepository;
import cc.easyandroid.easycore.EAResult;
import cc.easyandroid.easycore.EasyCall;
import cc.easyandroid.easycore.EasyResponse;
import cc.easyandroid.easylog.EALog;
import cc.easyandroid.easymvp.exception.EasyException;
import okhttp3.Request;

/**
 * Fetches the list of tasks.
 */
public class EasyHttpUseCase<T> extends UseCase<EasyHttpUseCase.RequestValues, EasyHttpUseCase.ResponseValue<T>> {

    EasyCall<T> lastEasyCall;//记录最后一个easycall
    public final EasyHttpRepository mEasyHttpRepository;

    private void cancelRequest() {
        if (lastEasyCall != null && !lastEasyCall.isCancel()) {
            lastEasyCall.cancel();
        }
    }

    public EasyHttpUseCase(EasyHttpRepository easyHttpRepository) {
        mEasyHttpRepository = easyHttpRepository;
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        cancelRequest();
        Request request = values.getRequest();
        Type type = values.getType();
        /**
         * 请求后每次记住easycall，防止重复调用，第二次进来会检测之前的是否完成，如果没有就调用cancelRequest取消之前的请求
         */
        lastEasyCall = mEasyHttpRepository.executeRequest(request, type, new EasyHttpRepository.HttpRequestCallback<T>() {
            @Override
            public void onResponse(EasyResponse<T> easyResponse) {
                T t = easyResponse != null ? easyResponse.body() : null;
                String defaultMessage = easyResponse != null ? easyResponse.message() : "";//"服务器或网络异常";
                EALog.e("EasyAndroid", "tag=" + values.getTag() + "   t=" + t);
                if (t == null) {
                    onFailure(new EasyException(defaultMessage));
                    return;
                } else if (t instanceof EAResult) {
                    EAResult kResult = (EAResult) t;
                    if (!kResult.isSuccess()) {
                        String errorMessage = kResult.getEADesc();
                        onFailure(new EasyException(errorMessage));
                        return;
                    }
                }
                getUseCaseCallback().onSuccess(new EasyHttpUseCase.ResponseValue<>(values.getTag(), t));
            }

            @Override
            public void onFailure(Throwable t) {
                getUseCaseCallback().onError(t);
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Request request;
        private final Object tag;
        private final Type type;

        public RequestValues(Object tag, Request request, Type type) {
            this.tag = tag;
            this.request = request;
            this.type = type;
        }

        public Object getTag() {
            return tag;
        }

        public Request getRequest() {
            return request;
        }

        public Type getType() {
            return type;
        }
    }

    public static final class ResponseValue<T> implements UseCase.ResponseValue {

        private final T data;
        private final Object tag;

        public ResponseValue(Object tag, T data) {
            this.data = data;
            this.tag = tag;
        }

        public Object getTag() {
            return tag;
        }

        public T getData() {
            return data;
        }
    }
}
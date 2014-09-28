package com.kypeli.aquarium.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JacksonRequest<T> extends Request<T> {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private final Response.ErrorListener errorListener;


    public JacksonRequest(String url,
                       Class<T> clazz,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        this.clazz = clazz;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String json = new String(
                    networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers)
            );

            return Response.success(jsonMapper.readValue(json, clazz), HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }  catch (JsonParseException e) {
            return Response.error(new ParseError(e));
        } catch (JsonMappingException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        this.listener.onResponse(response);
    }
}

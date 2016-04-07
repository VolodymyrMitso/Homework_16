package mitso.v.homework_16.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mitso.v.homework_16.constants.Constants;
import mitso.v.homework_16.enums.TypeRequest;
import mitso.v.homework_16.interfaces.IResponseListener;
import mitso.v.homework_16.models.Cat;

public final class AsyncRequest<T> extends AsyncTask<String, Void, T> {

    private Class<T> classType;
    private TypeRequest typeRequest;
    private IResponseListener<T> responseListener;
    private HashMap<String, Object> paramsRequest = new HashMap<>();
    private boolean isSuccess;

    public AsyncRequest<T> classType(final Class<T> classType) {
        this.classType = classType;
        return this;
    }

    public AsyncRequest<T> typeRequest(final TypeRequest typeRequest) {
        this.typeRequest = typeRequest;
        return this;
    }

    public AsyncRequest<T> addParam(final String key, final Object value) {
        this.paramsRequest.put(key, value);
        return this;
    }

    public AsyncRequest<T> responseListener(final IResponseListener<T> responseListener) {
        this.responseListener = responseListener;
        return this;
    }


    @Override
    protected T doInBackground(String... params) {
        final String requestUrl = params[0];
        StringBuilder urlParams = new StringBuilder();

        boolean isFirstParameter = true;
        for (String key : paramsRequest.keySet()) {
            urlParams
                    .append(isFirstParameter ? "?" : "&")
                    .append(key).append("=")
                    .append(paramsRequest.get(key));
            isFirstParameter = false;
        }

        try {
            return executeRequest(requestUrl, urlParams.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        if (responseListener != null)
            responseListener.onFinish(isSuccess, t);
    }

    private T executeRequest(final String requestUrl, final String requestParamsUrl) throws IOException {

        URL url = new URL(requestUrl + requestParamsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod(typeRequest.name());
        connection.connect();

        int code = connection.getResponseCode();

        if (code == 200) {
            isSuccess = true;
            return parseResponse(connection.getInputStream());
        } else {
            isSuccess = false;
        }

        return null;
    }

    private T parseResponse(final InputStream inputStream) throws IOException {

        if (classType.getName().equals(Cat[].class.getName())) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                total.append(line);

            return (T) getResponseCatFromString(total.toString());
        } else if (classType.getName().equals(Bitmap.class.getName())) {
            return (T) BitmapFactory.decodeStream(inputStream);
        } else
            return null;
    }

    private Cat[] getResponseCatFromString(final String response) throws IOException {

        List<Cat> catList = new ArrayList<>();
        Cat cat = new Cat();

        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            parser.setInput(new StringReader(response));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals(Constants.KEY_URL))
                            cat.setUrl(parser.nextText());
                        if (name.equals(Constants.KEY_ID))
                            cat.setId(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals(Constants.KEY_IMAGE)) {
                            catList.add(cat);
                            cat = new Cat();
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        Cat[] catArray = new Cat[catList.size()];
        catArray = catList.toArray(catArray);

        return catArray;
    }
}
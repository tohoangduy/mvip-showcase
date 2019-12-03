package com.mq.myvtg.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XMLPullParserHandler<T> {

    private List<T> listObjects = new ArrayList<>();
    private T object;
    private boolean searchFirstObject = true;

    public void setSearchFirstObject(boolean searchFirstObject) {
        this.searchFirstObject = searchFirstObject;
    }

    public List<T> parse(String xmlString, Class<T> tClass) {
        InputStream stream = new ByteArrayInputStream(xmlString.getBytes());
        return parse(stream, tClass);
    }

    public List<T> parse(InputStream stream, Class<T> tClass) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            String className = tClass.getName();
            className = className.substring(className.lastIndexOf('.') + 1);
            boolean isFoundClassTag = false, isDone = false;
            String tagname = "";
            while (eventType != XmlPullParser.END_DOCUMENT && !isDone) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagname = parser.getName();
                        if (tagname.equalsIgnoreCase(className)) {
                            // create a new instance of employee
                            object = tClass.newInstance();
                            isFoundClassTag = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (isFoundClassTag) {
                            String text = parser.getText();
                            object.getClass().getField(tagname).set(object, text);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagname = parser.getName();
                        if (tagname.equalsIgnoreCase(className)) {
                            // add employee object to list
                            listObjects.add(object);
                            isFoundClassTag = false;
                            isDone = searchFirstObject;
                        } else {
                            tagname = "";
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return listObjects;
    }
}
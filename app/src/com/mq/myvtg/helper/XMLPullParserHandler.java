package com.mq.myvtg.helper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XMLPullParserHandler<T> {

    public List<T> parse(InputStream stream, Class<T> tClass) {
        List<T> listObjects = new ArrayList<>();
        T object = null;

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            String className = tClass.getName();
            boolean isFoundClassTag = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
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
                        if (tagname.equalsIgnoreCase(className)) {
                            // add employee object to list
                            listObjects.add(object);
                            isFoundClassTag = false;
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
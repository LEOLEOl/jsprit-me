package com.ourcode.models.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by LEOLEOl on 5/5/2017.
 */
public class Utilities {
    public static <T> T[] cloneArray(T[] original) {

        //get the class type of the original array we passed in and determine the type, store in arrayType
        Class<?> arrayType = original.getClass().getComponentType();

        //declare array, cast to (T[]) that was determined using reflection, use java.lang.reflect to create a new instance of an Array(of arrayType variable, and the same length as the original
        T[] copy = (T[])java.lang.reflect.Array.newInstance(arrayType, original.length);

        //Use System and arraycopy to copy the array
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

    public static String getAllTextUTF8(String filePath) throws IOException
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            String string = new String(encoded, StandardCharsets.UTF_8);
            return string;
        } catch (IOException e) {
            throw e;
        }
    }
}

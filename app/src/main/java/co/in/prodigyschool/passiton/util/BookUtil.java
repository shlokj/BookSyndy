package co.in.prodigyschool.passiton.util;

import android.content.Context;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.in.prodigyschool.passiton.Data.Book;

public class BookUtil {

    private static final String TAG = "BookUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String BOOK_URL_FMT = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_book_image.png?alt=media&token=ba51ce8f-91bd-4c2d-bdea-d527b0667fc8";

    private static final int MAX_IMAGE_NUM = 2;


    public static Book addBook(String userId, boolean isTextbook, String bookName, String bookDescription, int gradeNumber, int boardNumber,int bookPrice, String bookAddress){
    Book book = new Book();
    Random random = new Random();
    book = new Book(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress,getRandomImageUrl(new Random()));
    return book;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return BOOK_URL_FMT;
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }
}
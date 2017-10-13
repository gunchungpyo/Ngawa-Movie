# Ngawa Movie 

Popular Movie app as part of [Associate Android Developer Fast Track](https://www.udacity.com/course/associate-android-developer-fast-track--nd818)

## Current Features
A app to help users discover popular or top rated movies and save it into their favorite movies.

### The app will
* Present the user with a grid arrangement of movie posters upon launch.
* Allow user to change sort order via a setting: by most popular, by highest-rated or favorite
* Allow user to tap on a movie poster and transition to a details screen with additional information
* Allow user to play trailer video using youtube or browser
* Allow user to see review from community
* Allow user to save movie into favorite and be able to see list of favorite movie

## Used Libraries
* [OkHttp](https://github.com/square/okhttp)
* [Picasso](http://square.github.io/picasso/)
* [Parceler](https://github.com/johncarl81/parceler)

## Important!
Please use your API_KEY to access [themoviedb API](https://www.themoviedb.org/) , you can change it in [here](https://github.com/gunchungpyo/Ngawa-Movie/blob/master/app/src/main/java/com/viv/gunchung/ngawamovie/utilities/MovieUtils.java#L38)

## Referance

### stage 2
Comparison Image framwork:
https://medium.com/@multidots/glide-vs-picasso-930eed42b81d
https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en

Dynamic layout
```
public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
}
```
Endless scrolling
https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
```
scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Load next page of movies
                Bundle queryBundle = new Bundle();
                queryBundle.putString(DISCOVER_STATE_EXTRA, mDefaultState);
                queryBundle.putInt("PAGE_NO", pageNo);
                getSupportLoaderManager().restartLoader(loaderId, queryBundle, callback);
            }
        };
// Associate RecyclerView with the EndlessRecyclerViewScrollListener
mMovieRecyclerView.setOnScrollListener(scrollListener);
```
JSON Parser:
https://github.com/google/gson
http://guides.codepath.com/android/leveraging-the-gson-library
https://futurestud.io/tutorials/gson-getting-started-with-java-json-serialization-deserialization
https://onelonecoder.wordpress.com/2015/04/16/andorid-restful-client-using-retrofit-gson/


### Stage 1
Some references to get started on the UI design:
https://developer.android.com/training/material/index.html
https://guides.codepath.com/android/Design-Support-Library
https://android-developers.googleblog.com/2015/05/android-design-support-library.html
https://www.udacity.com/course/material-design-for-android-developers--ud862

You can have different column count depending on the orientation (landscape or portrait), this will make your app more user-friendly:
http://stackoverflow.com/questions/29579811/changing-number-of-columns-with-gridlayoutmanager-and-recyclerview

Separate async task class:
http://stackoverflow.com/questions/15487807/android-development-having-an-asynctask-in-a-separate-class-file
http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/

Parcel:
https://guides.codepath.com/android/using-parcelable
http://www.101apps.co.za/index.php/articles/using-android-s-parcelable-class-a-tutorial.html
http://www.developerphil.com/parcelable-vs-serializable/
https://stackoverflow.com/questions/10975239/use-parcelable-to-pass-an-object-from-one-android-activity-to-another

Butterknife:
https://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
http://jakewharton.github.io/butterknife/
http://www.thekeyconsultant.com/2013/09/5-reasons-you-should-use-butterknife.html
https://www.youtube.com/watch?v=1N9KveJ-FU8&t=715s

Launch Mode:
https://developer.android.com/guide/components/tasks-and-back-stack.html#TaskLaunchModes
http://stackoverflow.com/a/15933890

Image Error handling:
http://stackoverflow.com/a/22786818
https://futurestud.io/tutorials/picasso-placeholders-errors-and-fading

minSDK:
https://medium.com/google-developers/picking-your-compilesdkversion-minsdkversion-targetsdkversion-a098a0341ebd#.6dr5y33tm

Recycle View:
https://guides.codepath.com/android/using-the-recyclerview
https://www.youtube.com/watch?v=LqBlYJTfLP4

HttpClient:
https://medium.com/android-news/android-networking-i-okhttp-volley-and-gson-72004efff196#.lslh887a1
https://packetzoom.com/blog/which-android-http-library-to-use.html

Some notes:
1. First, they separate code from external entities such as images and strings. Such separation is a good thing because it keeps the code focused and uncluttered.
2. Resources are efficient and fast. XML resources are compiled into a binary format. This makes them friendly at development time, without being slow at runtime.
3. Resources enable the support of dynamic loading at runtime based on various environment properties such as language, screen configuration, and hardware capability. This enables internationalization and localization.


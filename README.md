# nested-recycler-view

<p>
Implemented a vertical recylcer view with nested horizontal recycler view.<br/>
User can scroll vertically and horizontally through the grid of views.
</p>

# Application description
<p>
Simple application which shows movies list on recyclerView (parent recycler) and related movies inside another nested recyclerView (child recycler).
</p>

<p>
When user clicks on movie item it loads the details page for that movie and simultaneously tries to load related movie list (for the same genre(s), on which a click happen). After that if user clicks back button and related movies for that movie were loaded successfully a new horizontal recyclerView will be created for related items and dynamically will be added to parent recyclerView below the item on which click event happened.
</p>
<p>
If user clicks on some other movie item the detail view for that movie will be loaded and simultaneously (in background) previously created child recyclerView (if there was one) will be removed and new child recyclerView will be created (which contains related movies to the newly clicked movie item) on the new position.
</p>

# Screenshots from movies app

`movie list page`<br/>
<img src="https://github.com/msahakyan/nested-recycler-view/blob/master/app/src/main/res/raw/movie_list_framed.png" width="240px" height="426px" style="float: left; display:inline; margin: 30px;" alt="main feed page"></img>

`similar movies page`<br/>
<img src="https://github.com/msahakyan/nested-recycler-view/blob/master/app/src/main/res/raw/similar_movies_framed.png" width="240px" height="426px" style="margin: 30px;" alt="main feed page with loaded similar movies"></img>

`movie detail page`<br/>
<img src="https://github.com/msahakyan/nested-recycler-view/blob/master/app/src/main/res/raw/movie_detail_framed.png" width="240px" height="426px" style="float: left; display:inline; margin: 30px;"  alt="detail page view"></img>

`search results view`<br/>
<img src="https://github.com/msahakyan/nested-recycler-view/blob/master/app/src/main/res/raw/search_results_framed.png" width="240px" height="426px" style="float: left; display:inline; margin: 30px;"  alt="main feed page with active search view"></img>

`integrated youtube player`<br/>
<img src="https://github.com/msahakyan/nested-recycler-view/blob/master/app/src/main/res/raw/youtube_player_framed.png" width="240px" height="426px" style="margin: 30px;" alt="integrated youtube player"></img>


# Used Libraries
<ul>
  <li><b>com.github.erd:android-volley:1.0.0</b> <i>Networking</i>
  <li><b>com.google.code.gson:gson:2.4</b> <i>Serializing</i>
  <li><b>com.jakewharton:butterknife:7.0.1</b> <i>View injection</i>
  <li><b>com.github.rahatarmanahmed:circularprogressview:2.4.0</b> <i>Circular progress view</i>
  <li> <b>com.mcxiaoke.viewpagerindicator:library:2.4.1</b> <i>View pager indicator</i>
  <li> <b>AmazonInsights-android-sdk-2.1.26.jar</b> <i>A/B-Testing</i>
  <li> <b>YouTubeAndroidPlayerApi.jar</b> <i>Youtube API support</i>  
</ul>

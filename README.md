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

# Movies list before loading related items  
<img src="https://cloud.githubusercontent.com/assets/11542701/11168902/fc4319b8-8ba3-11e5-8986-d3e62a591c92.png" width="240px" height="426px"></img>
<br/><br/>

# Movies list after loading related items
<img src="https://cloud.githubusercontent.com/assets/11542701/11168905/03947874-8ba4-11e5-8479-6b567719b8f0.png" width="240px" height="426px"></img>

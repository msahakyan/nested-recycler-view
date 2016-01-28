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
<img src="https://cloud.githubusercontent.com/assets/11542701/12661695/6cfc4f38-c61b-11e5-955a-f4764fa1c486.png" width="240px" height="426px" style="float: left; display:inline" alt="main feed page"></img>

<img src="https://cloud.githubusercontent.com/assets/11542701/12661709/83e49a8e-c61b-11e5-8f73-06a659b3c086.png" width="240px" height="426px"  alt="main feed page with loaded similar movies"></img>

<img src="https://cloud.githubusercontent.com/assets/11542701/12661712/8c920ed2-c61b-11e5-8ca2-b1419f19712f.png" width="240px" height="426px" style="float: left; display:inline"  alt="main feed page with active search view"></img>

<img src="https://cloud.githubusercontent.com/assets/11542701/12661719/93ac554c-c61b-11e5-92f2-d95a7d34c860.png" width="240px" height="426px"  alt="integrated youtube player"></img>

<img src="https://cloud.githubusercontent.com/assets/11542701/12661723/a0a11ba2-c61b-11e5-8ce7-10662877f98c.png" width="240px" height="426px" style="float: left; display:inline"  alt="detail page view"></img>

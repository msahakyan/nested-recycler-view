# nested-recycler-view

<p>
Implemented a vertical recylcer view with nested horizontal recycler view.<br/>
User can scroll vertically and horizontally through the grid of views.
</p>

# Application description
<p>
Simple application which shows movies list on recyclerView (parent recycler) and related movies inside another nested recyclerView (child recycler).
</p>

When user clicks on movie item it loads related movie list (for the same type, for ex: 'thriller') then creates a new horizontal recyclerView for loaded items and dynamically adds a newly generated child recycler view into parent recyclerView below the item which was clicked. 
If user clicks on some other movie item previously created child recyclerView will be removed and new child recyclerView will be created (which contains related movies to the newly clicked movie item) on the new position.       
# Movies list before loading related items  
<img src="https://cloud.githubusercontent.com/assets/11542701/11168902/fc4319b8-8ba3-11e5-8986-d3e62a591c92.png" width="240px" height="426px"></img>
<br/><br/>

# Movies list after loading related items
<img src="https://cloud.githubusercontent.com/assets/11542701/11168905/03947874-8ba4-11e5-8479-6b567719b8f0.png" width="240px" height="426px"></img>

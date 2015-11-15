# nested-recycler-view

Implemented simple application which shows movies list on recyclerView (parent recycler).

[Description]
  When user clicks on movie item it loads related movie list (for the same type, for ex: 'thriller') then creates new horizontal recyclerView (child recycler) for that loaded items and dynamically adds that newly generated child recycler view into parent recyclerView after item which was clicked. 
  If user clicks on some other movie item previously created child recyclerView will be removed and new child recyclerView will be created (which contains related movies to the newly clicked movie item) on the new position.       
# Movies list before  
<img src="https://cloud.githubusercontent.com/assets/11542701/11168902/fc4319b8-8ba3-11e5-8986-d3e62a591c92.png" width="240px" height="426px"></img>
<br/><br/>

# Movies list with nested related items   
<img src="https://cloud.githubusercontent.com/assets/11542701/11168905/03947874-8ba4-11e5-8479-6b567719b8f0.png" width="240px" height="426px"></img>

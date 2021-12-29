# GoogleMapWithRecyclerView
RecyclerView with the markers' details shows on the Google map, connecting with open-data APIs.

<img src="https://github.com/yuchiaoc/GoogleMapWithRecyclerView/blob/master/map0410.gif" data-canonical-
src="https://github.com/yuchiaoc/GoogleMapWithRecyclerView/blob/master/map0410.gif" height="400" />

### 使用技術
- 在Fragment中使用Google Map API
- 串接政府Open Data資料(使用Gson操作)，定位在地圖中，並以RecyclerView顯示
- 使用ClusterManager管理大量Marker，使縮小地圖時Marker達到群聚效果
- 點擊Marker時滑動到相應之RecyclerView Item

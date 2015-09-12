<html>
<head>
    <meta name="viewport" content="width=10%">
    <meta charset="utf-8">
    <title>Tweet map</title>
    <link rel="stylesheet" type="text/css"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css"/>
    <script src="jquery-2.0.3.min.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
    <!-- Optional theme -->
    <style>
        #map-canvas {
            height: 100%;
            width: 90%;
            margin: 0px;
            padding: 0px;
            margin: auto
        }
    </style>
</head>
<body>

<div class="navbar navbar-default navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.jsp">Tweet Map</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="${activeTab eq 'map' ? 'active' : ''}"><a
                        href="index.jsp"><span class="glyphicon glyphicon-globe"></span>
                    Map</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container"></div>
<div class="row"></div>
<div class="col-xs-12 col-md-6 col-lg-6">
    <form class="form-inline">
        <div class="form-group">
            <label for="keyword" class="control-label">Keyword filtering
            </label>

            <div class="input-group">
                <div class="input-group-addon">
                    <span class="glyphicon glyphicon-filter"></span>
                </div>

                <select id="keyWord" class="form-control">
                    <option>Choose a Tag</option>
                    <option value="love">Love</option>
                    <option value="music">Music</option>
                    <option value="happy">happy</option>
                    <option value="food">food</option>
                    <option value="dance">dance</option>
                </select>
            </div>
        </div>
    </form>
</div>

<!-- Server responses get written here -->
<div id="messages"></div>
<div class="row">
    <div id="map-canvas"></div>
</div>


<script type="text/javascript">
    $(function () {
        var markers = [];
        var map;
        initializeMap();
        function initializeMap() {
            var center = new google.maps.LatLng(37.7699298, -122.4469157);
            var mapOptions = {
                zoom: 2,
                center: center,
                mapTypeId: google.maps.MapTypeId.TERRAIN
            };
            map = new google.maps.Map(document.getElementById('map-canvas'),
                    mapOptions);

        }

        function addMarker(location) {
            var marker = new google.maps.Marker({
                position: location,
                map: map
            });
            markers.push(marker);
        }

        // Sets the map on all markers in the array.
        function setAllMap(map) {

            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(map);
            }

        }

        // Removes the markers from the map, but keeps them in the array.
        function clearMarkers() {
            setAllMap(null);
        }

        // Shows any markers currently in the array.
        function showMarkers() {
            setAllMap(map);
        }

        // Deletes all markers in the array by removing references to them.
        function deleteMarkers() {
            clearMarkers();
            markers = [];
        }


        //ajax
        function getData(keyword) {
            $.get("GetAllData",
                    {keyword: keyword},
                    function (jsonData) {
                        data = JSON.parse(jsonData);
                        displayData(data);
                    });
            setInterval(function () {
                $.get("GetData",
                        {keyword: keyword},
                        function (jsonData) {
                            data = JSON.parse(jsonData);
                            alert("new data:" + jsonData);
                            displayData(data);
                        })
            }, 4000);
            //window.alert("getData");
        }

        function displayData(data) {
            for (var i = 0; i < data.geo.length; i++) {
                var latLng = new google.maps.LatLng(data.geo[i].lat, data.geo[i].log);
                addMarker(latLng);
            }
            showMarkers();
            //window.alert("displayData");
        }

        /*
         function displayData(data)
         {
         for (var i = 0; i < data.geo.length; i++)
         {
         var latLng = new google.maps.LatLng(clientGeoJson.geo[i].lat,clientGeoJson.geo[i].log);
         //alert(clientGeoJson.geo[i].lat,clientGeoJson.geo[i].log);
         pointArray.push(latLng);

         }
         }
         */

        //listener
        $("#keyWord").change(function () {
            var keyword = $(this).val();
            deleteMarkers();
            getData(keyword);
        });


    });
</script>

</body>
</html>
$(function() {
"use strict";
	Cookies.set("tweetTimeStamp", "0");
	Cookies.set("scoreTimeStamp", "0");
	var tweets = [];
	var map;
	var curIntervalID = 0;
	var lastIntervalID = 0;
	initializeMap();
	function initializeMap() {
		var center = new google.maps.LatLng(37.7699298, -122.4469157);
		var mapOptions = {
			zoom : 2,
			center : center,
			mapTypeId : google.maps.MapTypeId.TERRAIN
		};
		map = new google.maps.Map(document.getElementById("map-canvas"),
				mapOptions);
	}

	// Add Marker to the Tweets Array and update the TimeStamp
	function addMarker(data) {
		var tweetID = data.tweetID;
		updateTweetCookie(data.tweetTimeStamp);
		// marker is not exist yet
		if (tweets[tweetID] == undefined) {
			if (data.SentimentScore != -65536) {
				updateScoreCookie(data.scoreTimeStamp);
			}
			createMarker(data);
		} else if (data.SentimentScore != -65536) {
			updateScoreCookie(data.scoreTimeStamp);
			updateScore(data);
		}
	}

	// invoked if the Marker is not exist
	function createMarker(data) {
		var location = new google.maps.LatLng(data.lat, data.log);
		var tweetID = data.tweetID;
		var pinImage = judgeColorLocal(data.SentimentScore);
		if(tweets[tweetID] == undefined){
			var tweet = new google.maps.Marker({
				position : location,
				animation : google.maps.Animation.DROP,
				map : map,
				icon : pinImage
			});
			tweets[tweetID] = tweet;
		}
		else{
			tweets[tweetID].setIcon(pinImage);
			tweets[tweetID].setAnimation(google.maps.Animation.BOUNCE);
			setTimeout(function() {tweets[tweetID].setAnimation(null);}, 750);
		}
	}

	// invoked if the Marker exist
	function updateScore(data) {
		createMarker(data);
	}

	// Add click Event
	var bindMarkerEvents = function(marker) {
		var infowindow = new google.maps.InfoWindow(  
			      { content: "test"  
			      });
		google.maps.event.addListener(marker, "rightclick", function(event) {
			get_marker_id(event.latLng);
			infowindow.open(map,marker);
		});
	};

	// determine the marker's color
	function judgeColor(score) {
		// TODO judge the color with the score
		var pinColor = "FE7569";
		if (score > 0) {
			pinColor = "00FFFF";
		} else if (score != -65536) {
			pinColor = "FF8C00";
		}
		var pinImage = new google.maps.MarkerImage(
				"http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|"
						+ pinColor, new google.maps.Size(21, 34),
				new google.maps.Point(0, 0), new google.maps.Point(10, 34));
		var pinShadow = new google.maps.MarkerImage(
				"http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
				new google.maps.Size(40, 37), new google.maps.Point(0, 0),
				new google.maps.Point(12, 35));
		var colorProp = new Array();
		colorProp['icon'] = pinImage;
		colorProp['shadow'] = pinShadow;
		return colorProp;
	}
	
	//use local marker image
	function judgeColorLocal(score){
		var imageString = "images/red-dot.png";
		if(score > 0){
			imageString = "images/green-dot.png";
		}
		else if(score != "-65536"){
			imageString = "images/yellow-dot.png";
		}
		var pinImage = new google.maps.MarkerImage(imageString);
		return pinImage;
	}

	// Sets the map on all markers in the array.
	function setAllMap(map) {
		for(var key in tweets){
			tweets[key].setMap(map);
		}
	}

	// Removes the markers from the map, but keeps them in the array.
	function clearMarkers() {
		setAllMap(null);
	}

	function showMarkers() {
		setAllMap(map);
	}

	// Deletes all markers in the array by removing references to them.
	function deleteMarkers() {
		clearMarkers();
		tweets = [];
	}

	function updateTweetCookie(tweetTimeStamp) {
		var cur_tweetTimeStamp = Cookies.get("tweetTimeStamp");
		if (tweetTimeStamp > cur_tweetTimeStamp) {
			Cookies.set("tweetTimeStamp", tweetTimeStamp);
		}
	}

	function updateScoreCookie(scoreTimeStamp) {
		var cur_scoreTimeStamp = Cookies.get("scoreTimeStamp");
		if (scoreTimeStamp > cur_scoreTimeStamp) {
			Cookies.set("scoreTimeStamp", scoreTimeStamp);
		}
	}

	function getData(keyword) {
		$.get("GetAllData", {
			keyword : keyword
		}, function(jsonData) {
			var data = JSON.parse(jsonData);
			displayData(data);
		});
		lastIntervalID = curIntervalID;
		curIntervalID = setInterval(function() {
			$.get("GetData", {
				keyword : keyword
			}, function(jsonData) {
				var data = JSON.parse(jsonData);
				displayData(data);
			})
		}, 4000);

		clearInterval(lastIntervalID);
	}

	function displayData(data) {
		for (var i = 0; i < data.geo.length; i++) {
			addMarker(data.geo[i]);
		}
//		showMarkers();
	}

	// Start to trigger server side event
	function getScoredData() {
		var eventSource = new EventSource("EventTrigger");// servlet_1: EventTrigger get data from DB
		eventSource.onmessage = function(event) {
			var myData = JSON.parse(event.data);
			displayData(myData);
		};
	}

	$("#keyWord").change(function() {
		var keyword = $(this).val();
		Cookies.set("keyword", keyword);
		deleteMarkers();
		Cookies.set("tweetTimeStamp", 0);
		Cookies.set("scoreTimeStamp", 0);
		getData(keyword);
		setTimeout(getScoredData(), 5000);
	});

	// button to start the TweetGet thread
	$("#startThread").click(
			function() {
				$.get("ThreadController", {
					"request" : "create"
				}, function(response) {
					$("#messages").text(
							"TweetGet thread is started ,id is " + response);
					$("#startThread").hide();
					$("#stopThread").show();
				});
			});

	// button to stop the TweetGet Thread
	$("#stopThread").click(function() {
		$.get("ThreadController", {
			"request" : "destroy"
		}, function(response) {
			$("#messages").text(response + " TweetGet thread is stopped");
			$("#stopThread").hide();
			$("#startThread").show();
		});
	});

});

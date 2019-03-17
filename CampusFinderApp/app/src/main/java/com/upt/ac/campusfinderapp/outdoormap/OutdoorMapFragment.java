package com.upt.ac.campusfinderapp.outdoormap;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.location.LatLngAcc;
import com.tomtom.online.sdk.map.BaseMarkerBalloon;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.SingleLayoutBalloonViewAdapter;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteType;
import com.tomtom.online.sdk.routing.data.TravelMode;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.routing.data.RouteResponse;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchQueryBuilder;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchResponse;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchResult;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchQueryBuilder;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResponse;
import com.tomtom.online.sdk.search.data.fuzzy.FuzzySearchResult;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchQueryBuilder;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchResponse;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.savedplaces.SavedPlaceRepository;
import com.upt.ac.campusfinderapp.utils.CurrentUserData;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OutdoorMapFragment extends Fragment implements OnMapReadyCallback,
        TomtomMapCallback.OnMapLongClickListener {

    private TomtomMap tomtomMap;

    private SearchApi searchApi;
    private RoutingApi routingApi;
    private Route route;
    private LatLng departurePosition;
    private LatLng destinationPosition;
    private LatLng wayPointPosition;
    private Icon departureIcon;
    private Icon destinationIcon;

    private ImageButton btnSearch;
    private EditText editTextPois;

    private Integer STANDARD_RADIUS = 3 * 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "OutdoorMap Fragment", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_outdoor_map, null);
        initTomTomServices();
        initUIViews(v);
        setupUIViewListeners();
        checkBundleForPlaceToSearch();
        return v;
    }

    private void initTomTomServices() {
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.tomtom_map);
        mapFragment.getAsyncMap(this);

        searchApi = OnlineSearchApi.create(getContext());
        routingApi = OnlineRoutingApi.create(getContext());
    }

    private void initUIViews(View view) {
        departureIcon = Icon.Factory.fromResources(getContext(), R.drawable.ic_map_route_departure);
        destinationIcon = Icon.Factory.fromResources(getContext(), R.drawable.ic_map_route_destination);
        btnSearch = view.findViewById(R.id.search_place_button);
        editTextPois = view.findViewById(R.id.search_place_edit_text);
    }

    private void setupUIViewListeners() {
        View.OnClickListener searchButtonListener = getSearchButtonListener();
        btnSearch.setOnClickListener(searchButtonListener);
    }

    private void checkBundleForPlaceToSearch() {
        if(getArguments() == null) {
            return;
        }
        String placeName = getArguments().getString("Place name");
        if(placeName == null) {
            return;
        }
        setLastKnownLocationAsDeparturePosition();
        searchNearMe(placeName);
    }

    @NonNull
    private View.OnClickListener getSearchButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearchClick(v);
            }

            String textToSearch;

            private void handleSearchClick(View v) {
                textToSearch = editTextPois.getText().toString();
                if(textToSearch.isEmpty()) {
                    return;
                }
                if (isRouteSet()) {
                    searchWithRoute(v);
                }
                else {
                    searchWithoutRoute();
                }
            }

            private  void searchWithRoute(View v) {
                Optional<CharSequence> description = Optional.fromNullable(v.getContentDescription());

                if (description.isPresent()) {
                    editTextPois.setText(description.get());
                    v.setSelected(true);
                }
                if (isWayPointPositionSet()) {
                    tomtomMap.clear();
                    drawRoute(departurePosition, destinationPosition);
                }
                tomtomMap.removeMarkers();
                searchAlongTheRoute(route, textToSearch);
            }

            private boolean isRouteSet() {
                return route != null;
            }

            private boolean isWayPointPositionSet() {
                return wayPointPosition != null;
            }

            private void searchAlongTheRoute(Route route, final String textToSearch) {
                final Integer MAX_DETOUR_TIME = 1000;
                final Integer QUERY_LIMIT = 10;
                searchApi.alongRouteSearch(new AlongRouteSearchQueryBuilder(textToSearch, route.getCoordinates(), MAX_DETOUR_TIME).withLimit(QUERY_LIMIT).build())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<AlongRouteSearchResponse>() {
                            @Override
                            public void onSuccess(AlongRouteSearchResponse response) {
                                displaySearchResults(response.getResults());
                            }

                            private void displaySearchResults(List<AlongRouteSearchResult> results) {
                                if (!results.isEmpty()) {
                                    for (AlongRouteSearchResult result : results) {
                                        createAndDisplayCustomMarker(result.getPosition(), result);
                                    }
                                    tomtomMap.zoomToAllMarkers();
                                } else {
                                    handleNoSearchResultError(textToSearch);
                                }
                            }

                            private void createAndDisplayCustomMarker(LatLng position, AlongRouteSearchResult result) {
                                String address = result.getAddress().getFreeformAddress();
                                String poiName = result.getPoi().getName();

                                BaseMarkerBalloon markerBalloonData = new BaseMarkerBalloon();
                                markerBalloonData.addProperty(getString(R.string.poi_name_key), poiName);
                                markerBalloonData.addProperty(getString(R.string.address_key), address);

                                MarkerBuilder markerBuilder = new MarkerBuilder(position)
                                        .markerBalloon(markerBalloonData)
                                        .shouldCluster(true);
                                tomtomMap.addMarker(markerBuilder);
                            }

                            @Override
                            public void onError(Throwable e) {
                                handleApiError(e);
                            }
                        });
            }

            private void searchWithoutRoute() {
                setLastKnownLocationAsDeparturePosition();
                searchNearMe(textToSearch);
            }
        };
    }

    private void setLastKnownLocationAsDeparturePosition() {
        Location location = getLastKnownLocation();
        departurePosition = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void searchNearMe(String textToSearch) {
        searchApi.search(new FuzzySearchQueryBuilder(textToSearch)
                .withPreciseness(new LatLngAcc(departurePosition, STANDARD_RADIUS)).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<FuzzySearchResponse>(){
                    @Override
                    public void onSuccess(FuzzySearchResponse fuzzySearchResponse) {
                        drawRouteBasedOnFirstFuzzySearchResponse(fuzzySearchResponse.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleNoSearchResultError(textToSearch);
                    }

                    private void drawRouteBasedOnFirstFuzzySearchResponse(@NonNull List<FuzzySearchResult> results) {
                        if(!results.isEmpty()) {
                            destinationPosition = results.get(0).getPosition();
                            drawRoute(departurePosition, destinationPosition);
                        }
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {
        this.tomtomMap = tomtomMap;
        this.tomtomMap.setMyLocationEnabled(true);
        this.tomtomMap.getUserLocation();
        this.tomtomMap.addOnMapLongClickListener(this);
        this.tomtomMap.getMarkerSettings().setMarkersClustering(true);
        this.tomtomMap.getMarkerSettings().setMarkerBalloonViewAdapter(createCustomViewAdapter());
    }

    private SingleLayoutBalloonViewAdapter createCustomViewAdapter() {
        return new SingleLayoutBalloonViewAdapter(R.layout.marker_custom_balloon) {
            @Override
            public void onBindView(View view, final Marker marker, BaseMarkerBalloon baseMarkerBalloon) {
                Button btnAddWayPoint = view.findViewById(R.id.btn_balloon_waypoint);
                Button btnSavePlace = view.findViewById(R.id.btn_balloon_save);
                TextView textViewPoiName = view.findViewById(R.id.textview_balloon_poiname);
                TextView textViewPoiAddress = view.findViewById(R.id.textview_balloon_poiaddress);
                textViewPoiName.setText(baseMarkerBalloon.getStringProperty(getString(R.string.poi_name_key)));
                textViewPoiAddress.setText(baseMarkerBalloon.getStringProperty(getString(R.string.address_key)));
                btnAddWayPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWayPoint(marker);
                    }

                    private void setWayPoint(Marker marker) {
                        wayPointPosition = marker.getPosition();
                        tomtomMap.clearRoute();
                        drawRouteWithWayPoints(departurePosition, destinationPosition, new LatLng[] {wayPointPosition});
                        marker.deselect();
                    }
                });

                btnSavePlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePlace(marker, baseMarkerBalloon);
                    }

                    private void savePlace(Marker marker, BaseMarkerBalloon baseMarkerBalloon) {
                        LatLng latLng = marker.getPosition();
                        String name = baseMarkerBalloon.getStringProperty(getString(R.string.poi_name_key));
                        String address = baseMarkerBalloon.getStringProperty(getString(R.string.address_key));
                        SavedPlaceRepository savedPlaceRepository = new SavedPlaceRepository(getContext());
                        savedPlaceRepository.savePlace(name, address, latLng);
                    }
                });
            }
        };
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if (isDeparturePositionSet() && isDestinationPositionSet()) {
            clearMap();
        } else {
            handleLongClick(latLng);
        }
    }

    private void clearMap() {
        tomtomMap.clear();
        departurePosition = null;
        destinationPosition = null;
        route = null;
    }

    private void handleLongClick(@NonNull LatLng latLng) {
        searchApi.reverseGeocoding(new ReverseGeocoderSearchQueryBuilder(latLng.getLatitude(), latLng.getLongitude()).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ReverseGeocoderSearchResponse>() {
                    @Override
                    public void onSuccess(ReverseGeocoderSearchResponse response) {
                        processResponse(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleApiError(e);
                    }

                    private void processResponse(ReverseGeocoderSearchResponse response) {
                        if (response.hasResults()) {
                            processFirstResult(response.getAddresses().get(0).getPosition());
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.geocode_no_results), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void processFirstResult(LatLng geocodedPosition) {
                        if (!isDeparturePositionSet()) {
                            setAndDisplayDeparturePosition(geocodedPosition);
                        } else {
                            destinationPosition = geocodedPosition;
                            tomtomMap.removeMarkers();
                            drawRoute(departurePosition, destinationPosition);
                        }
                    }

                    private void setAndDisplayDeparturePosition(LatLng geocodedPosition) {
                        departurePosition = geocodedPosition;
                        createMarkerIfNotPresent(departurePosition, departureIcon);
                    }
                });
    }

    private RouteQuery createRouteQuery(LatLng start, LatLng stop, LatLng[] wayPoints) {
        return (wayPoints != null) ?
                new RouteQueryBuilder(start, stop).withWayPoints(wayPoints).withRouteType(RouteType.FASTEST).build() :
                new RouteQueryBuilder(start, stop).withRouteType(RouteType.FASTEST).build();
    }

    private void drawRoute(LatLng start, LatLng stop) {
        wayPointPosition = null;
        drawRouteWithWayPoints(start, stop, null);
    }

    private void drawRouteWithWayPoints(LatLng start, LatLng stop, LatLng[] wayPoints) {
        RouteQuery routeQuery = createRouteQuery(start, stop, wayPoints);
        routeQuery.withTravelMode(TravelMode.PEDESTRIAN);
        routeQuery.withRouteType(RouteType.SHORTEST);
        routingApi.planRoute(routeQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<RouteResponse>() {

                    @Override
                    public void onSuccess(RouteResponse routeResult) {
                        displayRoutes(routeResult.getRoutes());
                        tomtomMap.displayRoutesOverview();
                    }

                    private void displayRoutes(List<FullRoute> routes) {
                        for (FullRoute fullRoute : routes) {
                            route = tomtomMap.addRoute(new RouteBuilder(
                                    fullRoute.getCoordinates()).startIcon(departureIcon).endIcon(destinationIcon).isActive(true));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleApiError(e);
                        clearMap();
                    }
                });
    }

    private void createMarkerIfNotPresent(LatLng position, Icon icon) {
        Optional optionalMarker = tomtomMap.findMarkerByPosition(position);

        if (!optionalMarker.isPresent()) {
            tomtomMap.addMarker(new MarkerBuilder(position)
                    .icon(icon));
        }
    }

    private Location getLastKnownLocation() {
        Location location;
        if(tomtomMap != null) {
            location = tomtomMap.getUserLocation();
            CurrentUserData.getInstance(getContext()).setCurrentLocation(location);
        }
        else {
            location = CurrentUserData.getInstance(getContext()).getCurrentLocation();
        }
        return location;
    }

    private boolean isDestinationPositionSet() {
        return destinationPosition != null;
    }

    private boolean isDeparturePositionSet() {
        return departurePosition != null;
    }

    private void handleApiError(Throwable e) {
        Log.d(getString(R.string.api_response_error),getString(R.string.general_error_message));
        Toast.makeText(getContext(), getString(R.string.api_response_error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show();
    }

    private  void handleNoSearchResultError(String textToSearch) {
        Toast.makeText(getContext(), String.format(getString(R.string.no_search_results), textToSearch), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

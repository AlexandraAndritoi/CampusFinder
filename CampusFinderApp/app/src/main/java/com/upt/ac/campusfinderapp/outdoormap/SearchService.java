package com.upt.ac.campusfinderapp.outdoormap;

import android.content.Context;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchQueryBuilder;
import com.tomtom.online.sdk.search.data.alongroute.AlongRouteSearchResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

class SearchService {
    private SearchApi searchApi;

    SearchService(Context context) {
        searchApi = OnlineSearchApi.create(context);
    }

    void searchAlongTheRoute(Route route, DisposableSingleObserver<AlongRouteSearchResponse> observer, String textToSearch) {
        final Integer MAX_DETOUR_TIME = 1000;
        final Integer QUERY_LIMIT = 10;
        searchApi.alongRouteSearch(new AlongRouteSearchQueryBuilder(textToSearch, route.getCoordinates(), MAX_DETOUR_TIME).withLimit(QUERY_LIMIT).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}

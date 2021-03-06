package io.github.louistsaitszho.erg2000.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.louistsaitszho.erg2000.R;
import io.github.louistsaitszho.erg2000.SearchParams;
import io.github.louistsaitszho.erg2000.interfaces.HideFAB;
import io.github.louistsaitszho.erg2000.interfaces.ScrollToTop;
import io.github.louistsaitszho.erg2000.realm.RealmController;
import io.github.louistsaitszho.erg2000.realm.realmObject.Record;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchFragment extends Fragment implements ScrollToTop{
  public static final String TAG = SearchFragment.class.getSimpleName();

  @BindView(R.id.nestedScrollView)  NestedScrollView nestedScrollView;
  @BindView(R.id.cvNoRecords)       CardView cvNoRecords;
  @BindView(R.id.tvNoRecords)       TextView tvNoRecords;
  @BindView(R.id.llFilters)         LinearLayout llFilters;
  @BindView(R.id.rbDistance)        RangeBar rbDistance;
  @BindView(R.id.rbRating)          RangeBar rbRating;
  @BindView(R.id.rbDuration)        RangeBar rbDuration;
  @BindView(R.id.rbStartDate)       RangeBar rbStartDate;

  long minDistance, maxDistance, minRating, maxRating, minDuration, maxDuration;
  Date minStartDate, maxStartDate;
  //TODO tag
  //TODO images

  public SearchFragment() {

  }

  public static SearchFragment newInstance() {
    SearchFragment fragment = new SearchFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();

    RealmResults<Record> records = RealmController.with(this).allRecords().sort("totalDistance", Sort.ASCENDING);
    if (records.size() > 1) {
      cvNoRecords.setVisibility(View.GONE);
      llFilters.setVisibility(View.VISIBLE);

      minDistance = records.first().getTotalDistance();
      maxDistance = records.last().getTotalDistance();
      Log.d(TAG, "min dist: " + minDistance + "/max dist: " + maxDistance);
      rbDistance.setTickEnd((float) maxDistance);
      rbDistance.setTickStart((float) minDistance);
      if (minDistance >= 1000 || (maxDistance - minDistance >= 10000)) {
        rbDistance.setTickInterval((float) 1000);
        rbDistance.setPinTextListener(new RangeBar.OnRangeBarTextListener() {
          @Override
          public String getPinValue(RangeBar rangeBar, int tickIndex) {
//            float distance = (minDistance + tickIndex*1000) / 1000;
//            String output = String.format(Locale.getDefault(), "%.1f", distance) + "k";
            long distance = (minDistance + tickIndex*1000) / 1000;
            String output = distance + "k";
            Log.d(TAG, "pin text: " + output);
            return output;
          }
        });
      } else if (maxDistance - minDistance >= 1000) {
        rbDistance.setTickInterval((float) 100);
      } else if (maxDistance - minDistance >= 100) {
        rbDistance.setTickInterval((float) 10);
      } else {
        rbDistance.setTickInterval((float) 1);
      }

      records = records.sort("averageRating", Sort.ASCENDING);
      minRating = records.first().getAverageRating();
      maxRating = records.last().getAverageRating();
      Log.d(TAG, "min rating: " + minRating + "/max rating: " + maxRating);
      rbRating.setTickEnd((float) maxRating);
      rbRating.setTickStart((float) minRating);

      records = records.sort("totalDuration", Sort.ASCENDING);
      minDuration = records.first().getTotalDuration();
      maxDuration = records.last().getTotalDuration();
      Log.d(TAG, "min dur: " + minDuration + "/max dur: " + maxDuration);

      records = records.sort("startDateTime", Sort.ASCENDING);
      minStartDate = records.first().getStartDateTime();
      maxStartDate = records.last().getStartDateTime();
      Log.d(TAG, "min sd: " + minStartDate.toString() + "/max sd: " + maxStartDate.toString());
    } else if (records.size() == 1) {
      llFilters.setVisibility(View.GONE);
      cvNoRecords.setVisibility(View.VISIBLE);
      tvNoRecords.setText(R.string.search_one_record);
      ((HideFAB) getActivity()).hideFAB();
    } else {
      llFilters.setVisibility(View.GONE);
      cvNoRecords.setVisibility(View.VISIBLE);
      tvNoRecords.setText(R.string.search_no_records);
      ((HideFAB) getActivity()).hideFAB();
    }
  }

  public SearchParams getSearchParams(){
    //TODO make sure you don't pass 0 to maxXXXXXXXX
    return new SearchParams(minDuration, maxDuration, minDistance, maxDistance, minRating, maxRating, null, minStartDate, maxStartDate);
  }

  @Override
  public boolean scrollToTop() {
    if (nestedScrollView.isSmoothScrollingEnabled()) {
      nestedScrollView.smoothScrollTo(0, 0);
      return true;
    }
    return false;
  }
}

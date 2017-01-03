package net.damroo.androidprototype.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.damroo.androidprototype.R;
import net.damroo.androidprototype.activity.helper.OrderListCustomSpinnerAdapter;
import net.damroo.androidprototype.events.DisplayEventType;
import net.damroo.androidprototype.events.DisplayOrderEvent;
import net.damroo.androidprototype.events.OrdersDownloadEvent;
import net.damroo.androidprototype.service.DBEventService;
import net.damroo.androidprototype.service.DaggerComponent;
import net.damroo.androidprototype.service.DaggerDaggerComponent;
import net.damroo.androidprototype.service.NetworkEventService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;
/*
                       // GLIDE
                        Glide.with(getContext())
                                .load(image.getUrl())
                                .placeholder(R.drawable.icon)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);

* */

public class OrderListViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private DaggerComponent network;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Spinner spinner;
    private Toolbar toolbar;

    @Inject
    public NetworkEventService nes;

    @Inject
    public DBEventService des;

    // changing in PROJECTION means change in setViewBinder and SimpleCursorAdapter
    static String[] PROJECTION = new String[]{"orderId as _id", "orderNumber", "displayPrice", "creationDate", "displayNameCity", "displayDateItems", "viewedOn",
            "returnedOn", "deliveredOn", "dispatchedOn", "pendingOn", "partialyDispatchedOn", "readyForDispatchOn", "inProcessOn", "rejectedOn", "closedOn", "paidOn", "partialyPaidOn", "imageUrl"}; // select(PROJECTION)

    static String SELECTION = null; // where(SELECTION), SELECTION = "orderNumber > 1300"

    @SuppressWarnings("SpellCheckingInspection")
    static String SORTORDER = "creationDate DESC"; // order by SORTORDER,  SORTORDER = "creationDate DESC"

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onStart() {
        super.onStart();

        // register eventbus everytime the app is started/restarted
        EventBus.getDefault().register(this);
        EventBus.getDefault().register(nes);
        EventBus.getDefault().register(des);

        // call event firstUse everytime we start/resume the app as default behaviour.
        EventBus.getDefault().post(new OrdersDownloadEvent("firstUse"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // dagger - register for di
        network = DaggerDaggerComponent.create();
        network.inject(this);

        // toolbar - dropdown for order filter
        toolbar = (Toolbar) findViewById(R.id.toolbarOrderList);
        spinner = (Spinner) findViewById(R.id.spinner_orders_type);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        addItemsToSpinner();

        final ListView listView = (ListView) findViewById(R.id.orderList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // please notice that some values/styles are set in view binder
        String[] fromColumns = {"orderNumber", "displayPrice", "displayDateItems", "displayPrice", "displayNameCity", "displayPrice", "imageUrl"};
        int[] toViews = {R.id.orderNumberOrderList, R.id.displayPriceOrderList, R.id.displayItemDateOrderList, R.id.paymentStatusOrderList, R.id.displayNameCityOrderList, R.id.shippingStatusOrderList, R.id.orderImageOrderList}; // The TextView in simple_list_item_1

        // Create an empty adapter, pass null for the cursor, then update it in onLoadFinished()
        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.content_orderlist, null,
                fromColumns, toViews, 0);

        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                int fontWeight = cursor.getString(cursor.getColumnIndex("viewedOn")) == null ? Typeface.BOLD : Typeface.NORMAL;
                if (view.getId() == R.id.orderNumberOrderList) {
                    TextView tv = (TextView) view;
                    tv.setTypeface(null, fontWeight);
                    tv.setText(cursor.getString(cursor.getColumnIndex("orderNumber")));
                    return true;
                }
                if (view.getId() == R.id.displayPriceOrderList) {
                    TextView tv = (TextView) view;
                    tv.setTypeface(null, fontWeight);
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayPrice")));
                    return true;
                }
                if (view.getId() == R.id.displayItemDateOrderList) {
                    TextView tv = (TextView) view;
                    tv.setTypeface(null, fontWeight);
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayDateItems")));
                    return true;
                }
                if (view.getId() == R.id.displayNameCityOrderList) {
                    TextView tv = (TextView) view;
                    tv.setTypeface(null, fontWeight);
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayNameCity")));
                    return true;
                }

                // set processing status with color
                if (view.getId() == R.id.shippingStatusOrderList) {
                    TextView tv = (TextView) view;
                    setShippingStatusTextView(tv, cursor);
                    return true;
                }

                // Set Payment status with color
                if (view.getId() == R.id.paymentStatusOrderList) {
                    TextView tv = (TextView) view;
                    setPaymentStatusTextView(tv, cursor);
                    return true;
                }

                // Set Image
                if (view.getId() == R.id.orderImageOrderList) {
                    ImageView iv = (ImageView) view;
                    Glide.with(view.getContext())
                            .load(cursor.getString(cursor.getColumnIndex("imageUrl")))
                            .placeholder(R.drawable.icon)
                            .skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv);
                    return true;
                }


                return false;
            }
        });
        listView.setAdapter(simpleCursorAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);

        // permanent progressbar in the bottom of the list view.
        ProgressBar progressBar = new ProgressBar(this);
        listView.addFooterView(progressBar);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;

                swipeRefreshLayout.setRefreshing(false);
            }

            private void isScrollCompleted() {
                // check if scroll is completed and hit bottom.
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    Log.d("loading ... ", "older orders");
                    EventBus.getDefault().post(new OrdersDownloadEvent("downloadOldOrders"));
                }
            }

        });


    }


    private void addItemsToSpinner() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("All Orders"); // pos 0
        list.add("Open Orders"); // pos 1
        list.add("Awaiting"); // pos 2
        list.add("Inbox"); // pos 3
        OrderListCustomSpinnerAdapter spinAdapter = new OrderListCustomSpinnerAdapter(
                getApplicationContext(), list);
        spinner.setAdapter(spinAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();

                if (position == 1) {
                    OrderListViewActivity.SELECTION = "closedOn is null and rejectedOn is null and archivedOn is null and (paidOn is null or dispatchedOn is null) and (paidOn is null or deliveredOn is null)";
                } else if (position == 2) {
                    OrderListViewActivity.SELECTION = "closedOn is null and rejectedOn is null and archivedOn is null and returnedOn is null and deliveredOn is null and " +
                            "dispatchedOn  is null and pendingOn  is null and partialyDispatchedOn  is null and readyForDispatchOn  is null and inProcessOn is null and " +
                            "partialyPaidOn is null and  paidOn is null";
                } else if (position == 3) {
                    OrderListViewActivity.SELECTION = "viewedOn is null";
                } else {
                    OrderListViewActivity.SELECTION = null;
                }
                getLoaderManager().restartLoader(0, null, OrderListViewActivity.this);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse("content://net.damroo.androidprototype.provider/OrderModel"),
                PROJECTION, SELECTION, null, SORTORDER);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d("loading ... ", "newer orders");
        EventBus.getDefault().post(new OrdersDownloadEvent("downloadNewOrders"));
    }


    @Override
    public void onStop() {
        // unregister the eventbus
        EventBus.getDefault().unregister(nes);
        EventBus.getDefault().unregister(des);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }


    // trigger 'firstUse' event when sync button is clicked.
    public void getOrdersForFirstUse(View view) {
        EventBus.getDefault().post(new OrdersDownloadEvent("firstUse"));
    }

    // removes the progressbar set by swipeRefreshLayout (on swipe down gesture from top of the order-list).
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopLoadingAnimation(DisplayOrderEvent event) {
        if (event.type.equals(DisplayEventType.STOP_ANIMATION)) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    // helper method. sets text and style to textview.
    private void setPaymentStatusTextView(TextView tv, Cursor cursor) {
        tv.setText("");
        if (cursor.getString(cursor.getColumnIndex("rejectedOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.rejectedRed));
            tv.setText(R.string.rejected);
        } else if (cursor.getString(cursor.getColumnIndex("closedOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.finishedGray));
            tv.setText(R.string.closed);
        } else if (cursor.getString(cursor.getColumnIndex("paidOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.gorgeousGreen));
            tv.setText(R.string.paid);
        } else if (cursor.getString(cursor.getColumnIndex("partialyPaidOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.busyBlue));
            tv.setText(R.string.partiallypaid);
        } else {
            tv.setTextColor(getResources().getColor(R.color.waitingOrange));
            tv.setText(R.string.awaiting);
        }
        return;
    }

    // helper method. sets text and style to textview.
    private void setShippingStatusTextView(TextView tv, Cursor cursor) {
        tv.setText("");
        if (cursor.getString(cursor.getColumnIndex("returnedOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.rejectedRed));
            tv.setText(R.string.returned);
        } else if (cursor.getString(cursor.getColumnIndex("deliveredOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.finishedGray));
            tv.setText(R.string.delivered);
        } else if (cursor.getString(cursor.getColumnIndex("dispatchedOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.gorgeousGreen));
            tv.setText(R.string.dispatched);
        } else if (cursor.getString(cursor.getColumnIndex("pendingOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.pendingOrange));
            tv.setText(R.string.pending);
        } else if (cursor.getString(cursor.getColumnIndex("partialyDispatchedOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.busyBlue));
            tv.setText(R.string.partiallydispatched);
        } else if (cursor.getString(cursor.getColumnIndex("readyForDispatchOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.busyBlue));
            tv.setText(R.string.readyfordispatch);
        } else if (cursor.getString(cursor.getColumnIndex("inProcessOn")) != null) {
            tv.setTextColor(getResources().getColor(R.color.busyBlue));
            tv.setText(R.string.inprocess);
        } else {
            tv.setTextColor(getResources().getColor(R.color.waitingOrange));
            tv.setText(R.string.awaiting);
        }
        return;
    }

}

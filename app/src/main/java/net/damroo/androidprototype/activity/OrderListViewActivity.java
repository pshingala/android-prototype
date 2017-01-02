package net.damroo.androidprototype.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import net.damroo.androidprototype.activity.helper.OrderListCustomSpinnerAdapter;
import net.damroo.androidprototype.events.DisplayOrderEvent;
import net.damroo.androidprototype.events.DisplayEventType;
import net.damroo.androidprototype.events.OrdersDownloadEvent;
import net.damroo.androidprototype.R;
import net.damroo.androidprototype.service.DBEventService;
import net.damroo.androidprototype.service.DaggerDaggerComponent;
import net.damroo.androidprototype.service.DaggerComponent;
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

    private String LOG = "OrderListViewActivity";

    private DaggerComponent network;
    private SimpleCursorAdapter mAdapter;
    private Spinner spinner;
    private Toolbar toolbar;

    @Inject
    public NetworkEventService nes;

    @Inject
    public DBEventService des;

    // changing in PROJECTION means change in setViewBinder and SimpleCursorAdapter
    static String[] PROJECTION = new String[]{"orderId as _id", "orderNumber", "displayPrice", "creationDate", "displayNameCity", "displayDateItems", "viewedOn",
            "returnedOn", "deliveredOn", "dispatchedOn", "pendingOn", "partialyDispatchedOn", "readyForDispatchOn", "inProcessOn", "rejectedOn", "closedOn", "paidOn", "partialyPaidOn"}; // select(PROJECTION)

    static String SELECTION = null; // where(SELECTION), SELECTION = "orderNumber > 1300"

    @SuppressWarnings("SpellCheckingInspection")
    static String SORTORDER = "creationDate DESC"; // order by SORTORDER,  SORTORDER = "creationDate DESC"

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        EventBus.getDefault().register(nes);
        EventBus.getDefault().register(des);

        Log.d(LOG,"onStart");
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

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);

        final ListView listView = (ListView) findViewById(R.id.orderList);
        listView.setEmptyView(progressBar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // some column values / styles are set below so
        String[] fromColumns = {"orderNumber", "displayPrice", "displayDateItems", "displayPrice", "displayNameCity", "displayPrice", "displayPrice", "displayPrice"};
        int[] toViews = {R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text6, R.id.orderPaymentStatusBox, R.id.orderShippingStatusBox}; // The TextView in simple_list_item_1

        // Create an empty adapter, pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                R.layout.content_orderlist, null,
                fromColumns, toViews, 0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.text1) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("viewedOn")) == null) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);
                    }
                    tv.setText(cursor.getString(cursor.getColumnIndex("orderNumber")));
                    return true;
                }
                if (view.getId() == R.id.text2) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("viewedOn")) == null) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);
                    }
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayPrice")));
                    return true;
                }
                if (view.getId() == R.id.text3) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("viewedOn")) == null) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);
                    }
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayDateItems")));
                    return true;
                }
                if (view.getId() == R.id.text5) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("viewedOn")) == null) {
                        tv.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv.setTypeface(null, Typeface.NORMAL);
                    }
                    tv.setText(cursor.getString(cursor.getColumnIndex("displayNameCity")));
                    return true;
                }

                // set processing status with color
                if (view.getId() == R.id.text6) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("returnedOn")) != null) {
                        tv.setTextColor(Color.parseColor("#cc3333"));
                        tv.setText("RETURNED");
                    } else if (cursor.getString(cursor.getColumnIndex("deliveredOn")) != null) {
                        tv.setTextColor(Color.parseColor("#777777"));
                        tv.setText("DELIVERED");
                    } else if (cursor.getString(cursor.getColumnIndex("dispatchedOn")) != null) {
                        tv.setTextColor(Color.parseColor("#33aa33"));
                        tv.setText("DISPATCHED");
                    } else if (cursor.getString(cursor.getColumnIndex("pendingOn")) != null) {
                        tv.setTextColor(Color.parseColor("#ff7733"));
                        tv.setText("PENDING");
                    } else if (cursor.getString(cursor.getColumnIndex("partialyDispatchedOn")) != null) {
                        tv.setTextColor(Color.parseColor("#3366ff"));
                        tv.setText("PARTIALLY DISPATCHED");
                    } else if (cursor.getString(cursor.getColumnIndex("readyForDispatchOn")) != null) {
                        tv.setTextColor(Color.parseColor("#3366ff"));
                        tv.setText("READY FOR DISPATCH");
                    } else if (cursor.getString(cursor.getColumnIndex("inProcessOn")) != null) {
                        tv.setTextColor(Color.parseColor("#3366ff"));
                        tv.setText("IN PROCESS");
                    } else {
                        tv.setTextColor(Color.parseColor("#ff9933"));
                        tv.setText("AWAITING PROCESSING");
                    }
                    return true;
                }

                // Set Payment status with color
                if (view.getId() == R.id.text4) {
                    TextView tv = (TextView) view;
                    tv.setText("");
                    if (cursor.getString(cursor.getColumnIndex("rejectedOn")) != null) {
                        tv.setTextColor(Color.parseColor("#cc3333"));
                        tv.setText("REJECTED");
                    } else if (cursor.getString(cursor.getColumnIndex("closedOn")) != null) {
                        tv.setTextColor(Color.parseColor("#777777"));
                        tv.setText("CLOSED");
                    } else if (cursor.getString(cursor.getColumnIndex("paidOn")) != null) {
                        tv.setTextColor(Color.parseColor("#33aa33"));
                        tv.setText("PAID");
                    } else if (cursor.getString(cursor.getColumnIndex("partialyPaidOn")) != null) {
                        tv.setTextColor(Color.parseColor("#3366ff"));
                        tv.setText("PARTIALLY PAID");
                    } else {
                        tv.setTextColor(Color.parseColor("#ff9933"));
                        tv.setText("AWAITING PAYMENT");
                    }
                    return true;
                }

                // Set background color of boxes
                if (view.getId() == R.id.orderPaymentStatusBox) {

                    TextView tv = (TextView) view;
                    tv.setText("");
                    if (cursor.getString(cursor.getColumnIndex("rejectedOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#99cc3333"));
                    } else if (cursor.getString(cursor.getColumnIndex("closedOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#99777777"));
                    } else if (cursor.getString(cursor.getColumnIndex("paidOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#9933aa33"));
                    } else if (cursor.getString(cursor.getColumnIndex("partialyPaidOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#993366ff"));
                    } else {
                        tv.setBackgroundColor(Color.parseColor("#99ff9933"));
                    }
                    return true;
                }
                if (view.getId() == R.id.orderShippingStatusBox) {
                    TextView tv = (TextView) view;
                    if (cursor.getString(cursor.getColumnIndex("returnedOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bbcc3333"));
                    } else if (cursor.getString(cursor.getColumnIndex("deliveredOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bb777777"));
                    } else if (cursor.getString(cursor.getColumnIndex("dispatchedOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bb33aa33"));
                    } else if (cursor.getString(cursor.getColumnIndex("pendingOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bbff7733"));
                    } else if (cursor.getString(cursor.getColumnIndex("partialyDispatchedOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bb3366ff"));
                    } else if (cursor.getString(cursor.getColumnIndex("readyForDispatchOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bb3366ff"));
                    } else if (cursor.getString(cursor.getColumnIndex("inProcessOn")) != null) {
                        tv.setBackgroundColor(Color.parseColor("#bb3366ff"));
                    } else {
                        tv.setBackgroundColor(Color.parseColor("#bbff9933"));
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);

        ProgressBar footer = new ProgressBar(this);
        listView.addFooterView(footer);

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

//              if (totalItem - currentFirstVisibleItem == totalItem && this.currentScrollState == SCROLL_STATE_IDLE) {}
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    Log.d("Loading:", "bottom");
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
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d("Loading:", "top");
        EventBus.getDefault().post(new OrdersDownloadEvent("downloadNewOrders"));
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(nes);
        EventBus.getDefault().unregister(des);
        EventBus.getDefault().unregister(this);

        Log.d("state:", "onStop");
        super.onStop();
    }


    public void getOrdersForFirstUse(View v){
        EventBus.getDefault().post(new OrdersDownloadEvent("firstUse"));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopLoadingAnimation(DisplayOrderEvent event) {
        if (event.type.equals(DisplayEventType.STOP_ANIMATION)) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}

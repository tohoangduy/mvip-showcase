package com.mq.myvtg.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mq.myvtg.R;
import com.mq.myvtg.adapter.AdapterCustomer;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.DateRangePickerDialog;
import com.mq.myvtg.dialog.FilterDialog;
import com.mq.myvtg.listener.OnScrollToLoadMoreListener;
import com.mq.myvtg.model.dto.LstHisScan;
import com.mq.myvtg.util.DateUtil;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;
import com.mq.myvtg.util.XMLPullParserHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrgmtSearchInformation extends BaseFrgmt
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int PAGE_SIZE = 20;
    private static final String RANK_SEARCH_ALL = "ALL";

    private View highLightToDay, highLightYesterday, highLightLast1M, calendarFilter;
    private RecyclerView rcCustomer;
    private DateRangePickerDialog dialogDateRangePicker;
    TextView tvNotiNoResult, tvRank, tvCalendarFilter;

    FilterDialog filterDialog;
    private String fromDate, toDate;
    private final List<LstHisScan> datasetCustomers = new ArrayList<>();
    private final AdapterCustomer adapterCustomer = new AdapterCustomer(datasetCustomers);
    private int currentPageIndex = 0;
    private int totalPages = 0;
    private String mRankFilter = RANK_SEARCH_ALL, mScanTypeFilter = RANK_SEARCH_ALL;
    private List<String> types;

    public static FrgmtSearchInformation newInstance() {
        return new FrgmtSearchInformation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init types
        types = Arrays.asList(getResources().getStringArray(R.array.scan_types));
    }

    private void getListRank() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_GET_LIST_RANK, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                String valueParsed = resValue.replaceAll("<listRank>", "");
                ArrayList<String> ranks = new ArrayList<>(
                        Arrays.asList(
                                valueParsed.split("</listRank>")
                        )
                );
                ranks.add(0, RANK_SEARCH_ALL);

                initFilterDialog(ranks, types);
                UIHelper.hideProgress();
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                LogUtil.d("Get list rank fail");
                UIHelper.hideProgress();
            }
        });
    }

    private void initFilterDialog(List<String> ranks, List<String> types) {
        filterDialog = new FilterDialog(getContext(), ranks, 0, types, 0, new FilterDialog.DialogListener() {

            @Override
            public void ready(String rankSelected, String typeSelected) {
                Log.d(TAG, "Rank selected: ".concat(rankSelected));
                Log.d(TAG, "Type selected: ".concat(typeSelected));

                mRankFilter = rankSelected;
                mScanTypeFilter = typeSelected;
                currentPageIndex = 0;

                getCustomerList();
            }

            @Override
            public void cancelled() {
                Log.d(TAG, "popup cancelled");
            }
        });
        filterDialog.setTitle(R.string.filter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_search_information, container, false);

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.transaction_information);

        // get references view
        View btnBack = contentView.findViewById(R.id.btnBack);
        highLightToDay = contentView.findViewById(R.id.highLightToDay);
        highLightYesterday = contentView.findViewById(R.id.highLightYesterday);
        highLightLast1M = contentView.findViewById(R.id.highLightLast1M);
        View tabToDay = contentView.findViewById(R.id.tabToDay);
        View tabYesterday = contentView.findViewById(R.id.tabYesterday);
        View tabLast1M = contentView.findViewById(R.id.tabLast1M);
        rcCustomer = contentView.findViewById(R.id.rcCustomer);
        calendarFilter = contentView.findViewById(R.id.calendarFilter);
        tvCalendarFilter = contentView.findViewById(R.id.tvCalendarFilter);
        View rankFilter = contentView.findViewById(R.id.rankFilter);
        tvNotiNoResult = contentView.findViewById(R.id.tvNotiNoResult);
        tvRank = contentView.findViewById(R.id.tvRank);

        // init values default on views
        Date currentDate = new Date();
        String dateRangeDisplay = DateUtil.date2String(currentDate.getTime(), "dd/MM/yyyy - dd/MM/yyyy");
        tvCalendarFilter.setText(dateRangeDisplay);
        rcCustomer.setAdapter(adapterCustomer);


        // set listeners
        rankFilter.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        calendarFilter.setOnClickListener(this);
        tabToDay.setOnClickListener(this);
        tabYesterday.setOnClickListener(this);
        tabLast1M.setOnClickListener(this);
        rcCustomer.addOnScrollListener(new OnScrollToLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (currentPageIndex < totalPages) {
                    ++currentPageIndex;
                    getCustomerList();
                }
            }
        });

        dialogDateRangePicker = new DateRangePickerDialog(getActivity(), new DateRangePickerDialog.DialogListener() {
            @Override
            public void onCompleted(String dateFrom, String dateTo) {
                LogUtil.d("Date from: " + dateFrom);
                LogUtil.d("Date to: " + dateTo);

                fromDate = dateFrom;
                toDate = dateTo;
                currentPageIndex = 0;

                highLightToDay.setVisibility(View.INVISIBLE);
                highLightYesterday.setVisibility(View.INVISIBLE);
                highLightLast1M.setVisibility(View.INVISIBLE);
                updateRangeDateView();
                getCustomerList();
            }

            @Override
            public void onCancelled() {

            }
        });

        getListRank();
        // set default search today
        fromDate = DateUtil.date2String(new Date(), DateUtil.DateFormat.ddMMyyyy);
        toDate = DateUtil.date2String(new Date(), DateUtil.DateFormat.ddMMyyyy);
        currentPageIndex = 0;
        getCustomerList();

        return contentView;
    }

    private void getCustomerList() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("rank", mRankFilter.equalsIgnoreCase(RANK_SEARCH_ALL) ? "" : mRankFilter); // to search all, must be set rank type is empty
        params.put("type", mScanTypeFilter.equalsIgnoreCase(RANK_SEARCH_ALL) ? "" : types.indexOf(mScanTypeFilter) - 1); // to search all, must be set rank type is empty
        params.put("fromDate", fromDate == null ? "" : fromDate);
        params.put("toDate", toDate == null ? "" : toDate);
        params.put("isdnPartner", Client.getInstance().userLogin.username);
        params.put("pageIndex", currentPageIndex);
        params.put("pageSize", PAGE_SIZE);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_GET_HIS_SCAN_VIP, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                XMLPullParserHandler<LstHisScan> parser = new XMLPullParserHandler<>();
                parser.setSearchFirstObject(false);

                int totalRecords;
                final String TOTAL_RECORDS_TAG_NAME = "total";
                String openTotalRecordsTag = "<" + TOTAL_RECORDS_TAG_NAME + ">";
                String closeTotalRecordsTag = "</" + TOTAL_RECORDS_TAG_NAME + ">";
                if (resValue.contains(openTotalRecordsTag)) {
                    String strTotalRecords = resValue.substring(
                            resValue.indexOf(openTotalRecordsTag) + openTotalRecordsTag.length(),
                            resValue.indexOf(closeTotalRecordsTag)
                    );
                    totalRecords = Integer.parseInt(strTotalRecords);
                    LogUtil.d("totalRecords = " + totalRecords);
                    totalPages = (int) Math.ceil(totalRecords / PAGE_SIZE);
                    LogUtil.d("totalPages = " + totalPages);

                    tvNotiNoResult.setVisibility(totalRecords == 0 ? View.VISIBLE : View.GONE);
                }

                if (currentPageIndex == 0) {
                    datasetCustomers.clear();
                }
                datasetCustomers.addAll(parser.parse(resValue, LstHisScan.class));
                adapterCustomer.notifyDataSetChanged();

                UIHelper.hideProgress();

            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                UIHelper.hideProgress();
            }
        }
    );
}

    @Override
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.btnBack:
                goBack();
                break;

            case R.id.calendarFilter:
                dialogDateRangePicker.show(fromDate, toDate);
                break;

            case R.id.rankFilter:
                showPopupSelectRanks();
                break;

            case R.id.tabToDay:
                highLightToDay.setVisibility(View.VISIBLE);
                highLightYesterday.setVisibility(View.INVISIBLE);
                highLightLast1M.setVisibility(View.INVISIBLE);

                fromDate = getToDayString();
                toDate = getToDayString();
                currentPageIndex = 0;

                updateRangeDateView();
                getCustomerList();
                break;

            case R.id.tabYesterday:
                highLightToDay.setVisibility(View.INVISIBLE);
                highLightYesterday.setVisibility(View.VISIBLE);
                highLightLast1M.setVisibility(View.INVISIBLE);

                fromDate = getYesterdayString();
                toDate = getToDayString();
                currentPageIndex = 0;

                updateRangeDateView();
                getCustomerList();
                break;

            case R.id.tabLast1M:
                highLightToDay.setVisibility(View.INVISIBLE);
                highLightYesterday.setVisibility(View.INVISIBLE);
                highLightLast1M.setVisibility(View.VISIBLE);

                fromDate = getLast30DaysString();
                toDate = getToDayString();
                currentPageIndex = 0;

                updateRangeDateView();
                getCustomerList();
                break;
        }
    }

    private void updateRangeDateView() {
        tvCalendarFilter.setText(
                fromDate.concat(" - ").concat(toDate)
        );
    }

    private void showPopupSelectRanks() {
        if (filterDialog != null) filterDialog.show();
    }

    private String getToDayString () {
        return DateUtil.date2String(new Date(), DateUtil.DateFormat.ddMMyyyy);
    }

    private String getYesterdayString () {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return DateUtil.date2String(cal.getTime(), DateUtil.DateFormat.ddMMyyyy);
    }

    private String getLast30DaysString () {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        return DateUtil.date2String(cal.getTime(), DateUtil.DateFormat.ddMMyyyy);
    }

    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
        String selectedRank = ((TextView) view).getText().toString();
        LogUtil.d("rank selected is " + selectedRank);
        currentPageIndex = 0;
        getCustomerList();
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){
        LogUtil.d("Nothing selected!");
    }
}

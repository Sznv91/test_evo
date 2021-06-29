package ru.softvillage.fiscalizer.tabs.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.fiscalizer.tabs.fragments.ReceiptDetailFragment;
import ru.softvillage.fiscalizer.tabs.fragments.ReceiptFragment;
import ru.softvillage.fiscalizer.tabs.fragments.recyclerView.ReceiptItemAdapter;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

public class ReceiptViewModel extends ViewModel {
    private Context context;
    ReceiptFragment receiptFragment;
    private LinearLayoutManager layoutManager;
    Application app = EvoApp.getInstance();
    ReceiptItemAdapter adapter;
    List<ReceiptEntity> localCopyReceiptEntityListWithDate = new ArrayList<>();


    @SuppressLint("LongLogTag")
    Observer<List<ReceiptEntity>> observer = receiptEntities -> {
        List<ReceiptEntity> goodEntityListWithDate = injectDateEntity(receiptEntities);
        localCopyReceiptEntityListWithDate = goodEntityListWithDate;
        adapter.setItems(goodEntityListWithDate);
        if (receiptEntities.size() > 0 && receiptFragment != null) {
            receiptFragment.hideEmptyListStab();
        }
        Log.d(EvoApp.TAG + "_Db", receiptEntities.toString());
    };

    public void setReceiptFragment(ReceiptFragment fragment){
        receiptFragment = fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setLinearLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @SuppressLint("LongLogTag")
    public ReceiptItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new ReceiptItemAdapter(LayoutInflater.from(app.getApplicationContext()),
                    new ReceiptItemAdapter.itemClickInterface() {
                        @Override
                        public void clickClick(ReceiptEntity recipientEntity) {
                            /**
                             * Расчет Duration с момента последнего открытия фрагмена.
                             */
                            Duration durationLastOpenReceiptDetailFragment = new Duration(SessionPresenter.getInstance().getLastOpenReceiptDetailFragment().toDateTime(), LocalDateTime.now().toDateTime());
                            if (ReceiptDetailFragment.LOADER_TIME_SCREEN <= durationLastOpenReceiptDetailFragment.getMillis()) {
                                Log.d(EvoApp.TAG + "_Recycler", "click - click " + recipientEntity.getReceiptNumber());
                                Fragment fragment = ReceiptDetailFragment.newInstance(String.valueOf(recipientEntity.getSv_id()));
                                if (EvoApp.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    EvoApp.getInstance().getFragmentDispatcher().replaceFragment(fragment);
                                } else if (EvoApp.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    int count = EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().getBackStackEntryCount();
                                    Log.d(EvoApp.TAG + "_backStack", "Count back stack is: " + count);
                                    if (count == 0) {
                                        EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_statistic_fragment, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
                                    } else {
                                        EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_statistic_fragment, fragment).commit();
                                    }
                                }
                            }
                        }

                        @Override
                        public void pushOnDate(LocalDateTime date) {
                            Log.d(EvoApp.TAG + "_date_splitter", "Нажали на разделитель даты. Дата: " + date.toString());
                            MaterialPickerOnPositiveButtonClickListener<Long> selectListener = new MaterialPickerOnPositiveButtonClickListener<Long>() {
                                @Override
                                public void onPositiveButtonClick(Long selection) {
                                    DateTime dateTime = new DateTime(selection);
                                    LocalDateTime pickDate = dateTime.toLocalDateTime();
                                    pickDate.withTime(0, 0, 0, 0);
                                    int position = positionSearcher(localCopyReceiptEntityListWithDate, pickDate);
                                    if (position != -1) {
                                        Log.d(EvoApp.TAG + "_date_splitter", "Перемещаемся к позиции: " + position);
                                        layoutManager.scrollToPositionWithOffset(position, 0);
                                    }
                                }
                            };
                            getDatePickerDialog(selectListener, date);
                        }
                    });
            observeOnChangeDb();
        }
        return adapter;
    }

    private void observeOnChangeDb() {
        EvoApp.getInstance().getDbHelper().getAll().observeForever(observer);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCleared() {
        Log.d(EvoApp.TAG + "_LifeCycle", "Отписались от LiveData");
        EvoApp.getInstance().getDbHelper().getAll().removeObserver(observer);
        super.onCleared();
    }


    private List<ReceiptEntity> injectDateEntity(List<ReceiptEntity> receiptEntities) {
        if (receiptEntities.size() == 0) {
            return receiptEntities;
        }
        List<ReceiptEntity> result = new ArrayList<>();
        LocalDate lastProcessedDate = null;
        for (int i = 0; i < receiptEntities.size(); i++) {
            if (receiptEntities.get(i).getReceived().toLocalDate().equals(lastProcessedDate)) {
                result.add(receiptEntities.get(i));
            } else {
                lastProcessedDate = receiptEntities.get(i).getReceived().toLocalDate();
                ReceiptEntity dateSplitter = new ReceiptEntity();
                dateSplitter.setUuid(ReceiptItemAdapter.DATE_SPLITTER_NAME);
                LocalDateTime splitDate = receiptEntities.get(i).getReceived().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                dateSplitter.setReceived(splitDate);
                result.add(dateSplitter);
                result.add(receiptEntities.get(i));
            }
        }
        return result;
    }

    private int positionSearcher(List<ReceiptEntity> localCopyReceiptEntityListWithDate, LocalDateTime pickDate) {
        int result = -1;
        for (int i = 0; i < localCopyReceiptEntityListWithDate.size(); i++) {
            if (localCopyReceiptEntityListWithDate.get(i).getReceived().toLocalDate()
                    .equals(pickDate.toLocalDate())) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void getDatePickerDialog(MaterialPickerOnPositiveButtonClickListener<Long> listener, LocalDateTime selectedDate) {
        MaterialDatePicker.Builder<Long> calendarDatePicker = MaterialDatePicker.Builder.datePicker();
        calendarDatePicker.setCalendarConstraints(calendarDisableConstraints().build());
        calendarDatePicker.setTitleText("Даты печати чеков");
        LocalDateTime ldt = selectedDate.plusDays(1);
        calendarDatePicker.setSelection(ldt.toDateTime().getMillis());
        MaterialDatePicker<Long> pickerRange = calendarDatePicker.build();
        pickerRange.addOnPositiveButtonClickListener(listener);
        pickerRange.show(EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager(), pickerRange.toString());
    }

    private CalendarConstraints.Builder calendarDisableConstraints() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        constraintsBuilderRange.setValidator(new EnableDateValidator());
        return constraintsBuilderRange;
    }

    @SuppressLint("ParcelCreator")
    static class EnableDateValidator implements CalendarConstraints.DateValidator {

        @Override
        public boolean isValid(long date) {
            DateTime dateTime = new DateTime(date);
            LocalDate localDate = LocalDate.fromDateFields(dateTime.toDate());
            return EvoApp.getInstance().getDbHelper().getUniqueDate().contains(localDate);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
}
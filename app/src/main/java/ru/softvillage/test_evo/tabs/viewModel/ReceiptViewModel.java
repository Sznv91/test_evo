package ru.softvillage.test_evo.tabs.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.test_evo.tabs.fragments.ReceiptDetailFragment;
import ru.softvillage.test_evo.tabs.fragments.recyclerView.ReceiptItemAdapter;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class ReceiptViewModel extends ViewModel {
    private Context context;
    private LinearLayoutManager layoutManager;
    Application app = EvoApp.getInstance();
    ReceiptItemAdapter adapter;
    List<ReceiptEntity> localCopyReceiptEntityListWithDate = new ArrayList<>();


    @SuppressLint("LongLogTag")
    Observer<List<ReceiptEntity>> observer = receiptEntities -> {
        List<ReceiptEntity> goodEntityListWithDate = injectDateEntity(receiptEntities);
        localCopyReceiptEntityListWithDate = goodEntityListWithDate;
        adapter.setItems(goodEntityListWithDate);
        Log.d(EvoApp.TAG + "_Db", receiptEntities.toString());
    };

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
                            DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                /**
                                 *К месяцу прибавляем единицу. В Android счет месяцев ведется с 0
                                 */
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    LocalDateTime pickDate = LocalDateTime.parse(String.valueOf(year + "-"
                                            + (month + 1) + "-" +
                                            +dayOfMonth + "T00:00:00"));
                                    int position = positionSearcher(localCopyReceiptEntityListWithDate, pickDate);
                                    if (position != -1) {
                                        Log.d(EvoApp.TAG + "_date_splitter", "Перемещаемся к позиции: " + position);
                                        layoutManager.scrollToPositionWithOffset(position, 0);
                                    }
                                }
                            };

                            getDatePickerDialog(d, date);
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

    private void getDatePickerDialog(DatePickerDialog.OnDateSetListener d, LocalDateTime selectedDate) {
        new DatePickerDialog(context, d,
                selectedDate.getYear(),
                selectedDate.getMonthOfYear() - 1,
                selectedDate.getDayOfMonth())
                .show();
    }
}
package com.thedeveloperworldisyours.myvocabulary.words;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thedeveloperworldisyours.myvocabulary.R;
import com.thedeveloperworldisyours.myvocabulary.data.Word;
import com.thedeveloperworldisyours.myvocabulary.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordsFragment extends Fragment implements WordsContract.View, WordsRecyclerViewAdapter.WordItemListener {

    @BindView(R.id.words_frag_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.words_frag_no_data_text_view)
    TextView mNoDataTextView;

    @BindView(R.id.words_frag_refresh_layout)
    ScrollChildSwipeRefreshLayout mRefresh;

    @BindView(R.id.words_frag_filter_view)
    LinearLayout mFilterView;

    private WordsContract.Presenter mPresenter;
    private WordsRecyclerViewAdapter mAdapter;
    private List<Word> mWordList;

    private WordsInteractionListener mListener;

    public WordsFragment() {
        // Required empty public constructor
    }

    public static WordsFragment newInstance() {
        return new WordsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new WordsRecyclerViewAdapter(new ArrayList<Word>(0));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.words_frag, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.words_act_fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPresenter.addNewTask();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(active);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearLearnedWord();
                break;
            case R.id.menu_filter:
                mFilterView.setVisibility(View.VISIBLE);
                startAnimation(mFilterView);
                break;
            case R.id.menu_refresh:
                mPresenter.loadWords(true);
                break;
        }
        return true;
    }

    private void startAnimation(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slid_down));
    }

    public void finishAnimation(final View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slid_up));
        view.setVisibility(View.GONE);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showWords(List<Word> words) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoDataTextView.setVisibility(View.GONE);
        mAdapter.replaceData(words);
    }

    @Override
    public void showAddWord() {

    }

    @Override
    public void showWordDetailsUi(String wordId) {

    }

    @Override
    public void showWordMarkedComplete() {
        showMessage(getString(R.string.fragment_words_marked_complete));
    }

    @Override
    public void showWordMarkedActive() {
        showMessage(getString(R.string.fragment_words_marked_active));
    }

    @Override
    public void showLearnedWordsCleared() {
        mListener.onFragmentInteraction(getResources().getString(R.string.fragment_words_learned_words_cleared));
    }

    @Override
    public void showLoadingWordsError() {
        showMessage(getString(R.string.fragment_words_loading_words_error));
    }

    @Override
    public void showNoWords() {
        mNoDataTextView.setText(getString(R.string.fragment_word_no_active_word));
        mRecyclerView.setVisibility(View.GONE);
        mNoDataTextView.setVisibility(View.VISIBLE);

    }

    @Override
    public void showActiveFilterLabel() {
        mListener.onFragmentInteraction(getResources().getString(R.string.fragment_words_label_active));
    }

    @Override
    public void showLearnedFilterLabel() {
        mListener.onFragmentInteraction(getResources().getString(R.string.fragment_words_label_learned));
    }

    @Override
    public void showAllFilterLabel() {
        mListener.onFragmentInteraction(getResources().getString(R.string.fragment_words_label_all));
    }

    @Override
    public void showNoActiveWords() {
        mNoDataTextView.setText(getString(R.string.fragment_word_no_active_word));
        mRecyclerView.setVisibility(View.GONE);
        mNoDataTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoLearnedWords() {
        mNoDataTextView.setText(getString(R.string.fragment_word_no_learned_word));
        mRecyclerView.setVisibility(View.GONE);
        mNoDataTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccessfullySavedMessage() {
//        No now
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.words_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.words_frag_all_button)
    public void allWords() {
        filted(WordsFilterType.ALL_WORDS);
    }

    @OnClick(R.id.words_frag_active_button)
    public void activeWords() {
        filted(WordsFilterType.ACTIVE_WORDS);
    }

    @OnClick(R.id.words_frag_learned_button)
    public void learnWords() {
        filted(WordsFilterType.LEARNED_WORDS);
    }

    public void filted(WordsFilterType filterType) {
        mPresenter.setFiltering(filterType);
        mPresenter.loadWords(false);
        finishAnimation(mFilterView);
    }

    @Override
    public void setPresenter(WordsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onWordClick(int position, View v) {
        mPresenter.openWordDetails(mWordList.get(position));
    }

    @Override
    public void onCompleteWordClick(Word completedWord) {
        mPresenter.learnWord(completedWord);
    }

    @Override
    public void onActivateWordClick(Word activatedWord) {
        mPresenter.activateWord(activatedWord);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WordsInteractionListener) {
            //init the listener
            mListener = (WordsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

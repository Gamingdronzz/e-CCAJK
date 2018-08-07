package com.mycca.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mycca.R;

import static android.content.ContentValues.TAG;

public class BrowserFragment extends Fragment {


    WebView webView;
    ProgressBar progressBar;
    String url;
    ActionBar actionBar;
    private boolean hasStopped = false;
    ObjectAnimator progressAnimator;
    int previousProgress = 0;

    public BrowserFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        setHasOptionsMenu(true);
        init(view);
        setupWebview();
        hasStopped = false;
        webView.loadUrl(url);
        return view;
    }

    private void init(View view) {
        Bundle args = getArguments();
        if (getActivity() != null) {
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(1000);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        webView = view.findViewById(R.id.webview_cca);
        if (args != null) {
            url = args.getString("url");
        }
        progressAnimator = ObjectAnimator.ofInt(progressBar,"progress",0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_link:
                if (progressBar.getVisibility() == View.GONE)
                    webView.reload();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_browser, menu);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.O)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("UrlO", url);
                view.loadUrl(request.getUrl().toString());
                return true;

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Log.d("Url", url);
                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                setSubtitle("Some Error Occurred. Please Refresh");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setSubtitle("Loading...");
                progressBar.setVisibility(View.VISIBLE);
                progressAnimator.setIntValues(50,progressBar.getProgress());
                progressAnimator.setDuration(300);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();
                previousProgress = 50;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setSubtitle(view.getTitle());
                progressAnimator.setIntValues(1000, previousProgress);
                progressAnimator.setDuration(300);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();
                progressAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                });
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressAnimator.setIntValues(newProgress*10, previousProgress);
                progressAnimator.setDuration(100);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();
                previousProgress = newProgress*10;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    setSubtitle(title);
                }
            }
        });

    }

    void setSubtitle(String subtitle) {
        if (!hasStopped)
            actionBar.setSubtitle(Html.fromHtml("<font color='#000000'>" + subtitle + "</font>"));
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
        Log.v(TAG, "Going back");
    }

    public void stopLoading() {
        webView.stopLoading();
        hasStopped = true;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        stopLoading();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }
}

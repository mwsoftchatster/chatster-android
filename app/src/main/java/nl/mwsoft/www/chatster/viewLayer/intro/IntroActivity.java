package nl.mwsoft.www.chatster.viewLayer.intro;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.viewLayer.intro.adapter.IntroViewPagerAdapter;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.vpIntroActivity) ViewPager vpIntroActivity;
    private IntroViewPagerAdapter introViewPagerAdapter;
    @BindView(R.id.llDots) LinearLayout llDots;
    private ImageView[] dots;
    @BindView(R.id.btnIntroPrevious) Button btnIntroPrevious;
    @BindView(R.id.btnIntroNext) Button btnIntroNext;
    @BindView(R.id.btnIntroSkip) Button btnIntroSkip;
    private Unbinder unbinder;
    private RootCoordinator rootCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_intro);
        unbinder = ButterKnife.bind(this);
        DependencyRegistry.shared.inject(this);
        attachUI();
    }

    // region Configure

    public void configureWith(RootCoordinator rootCoordinator){
        this.rootCoordinator = rootCoordinator;
    }

    // endregion

    // region UI

    private void attachUI() {
        configureViewPager();
        showDots(0);
        vpIntroActivity.addOnPageChangeListener(myOnPageChangeListener);
    }

    private void configureViewPager() {
        introViewPagerAdapter = new IntroViewPagerAdapter(getSupportFragmentManager());
        vpIntroActivity.setOffscreenPageLimit(4);
        vpIntroActivity.setAdapter(introViewPagerAdapter);
    }

    private void showDots(int currentPosition){
        resetDots();
        showCurrentDots(currentPosition);
    }

    private void showCurrentDots(int currentPosition) {
        dots = new ImageView[6];

        for(int i = 0; i < 6; i++){
            dots[i] = new ImageView(IntroActivity.this);
            if(i == currentPosition){
                dots[i].setImageDrawable(ContextCompat.getDrawable(IntroActivity.this, R.drawable.active_dots));
            }else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(IntroActivity.this,R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);

            llDots.addView(dots[i],params);
        }
    }

    private void resetDots() {
        if(llDots != null){
            llDots.removeAllViews();
        }
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    // endregion

    // region ViewPager Page Change Logic

    public ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            runOnPageSelectedRoutine(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    private void runOnPageSelectedRoutine(int position) {
        showDots(position);
        setPreviousButtonVisibility(position);
        checkNextButton(position);
    }

    private void setPreviousButtonVisibility(int position) {
        if(position == ConstantRegistry.INTRO_VIEW_PAGER_FIRST_PAGE){
            makePreviousButtonInvisible();
        }else{
            makePreviousButtonVisible();
        }
    }

    private void makePreviousButtonInvisible() {
        if(btnIntroPrevious.getVisibility() == View.VISIBLE){
            btnIntroPrevious.setVisibility(View.GONE);
        }
    }

    private void checkNextButton(int position) {
        if(position == ConstantRegistry.INTRO_VIEW_PAGER_LAST_PAGE ){
            btnIntroNext.setText(ConstantRegistry.DONE);
        }

        if(position != ConstantRegistry.INTRO_VIEW_PAGER_LAST_PAGE && !btnIntroNext.getText().equals(getString(R.string.next))){
            btnIntroNext.setText(getString(R.string.next));
        }
    }

    @OnClick(R.id.btnIntroNext)
    public void  nextClickListener() {
        runOnClickNextRoutine();
    }

    private void runOnClickNextRoutine() {
        determineChangeNextButtonToDone();
        determineNavigateNextAfterOnClick();
    }

    private void determineNavigateNextAfterOnClick() {
        if(isLastPage()){
            navigateToWelcomeActivity();
        }else{
            makePreviousButtonVisible();
            determineNavigateToNextIntroFragment();
        }
    }

    private boolean isLastPage() {
        return vpIntroActivity.getCurrentItem() == ConstantRegistry.INTRO_VIEW_PAGER_LAST_PAGE;
    }

    private void determineNavigateToNextIntroFragment() {
        if(isNotLastPage()){
            vpIntroActivity.setCurrentItem(vpIntroActivity.getCurrentItem()+1);
        }
    }

    private boolean isNotLastPage() {
        return (vpIntroActivity.getCurrentItem()+1) < ConstantRegistry.INTRO_VIEW_PAGER_TOTAL_AMOUNT_PAGES;
    }

    private void makePreviousButtonVisible() {
        if(btnIntroPrevious.getVisibility() == View.GONE){
            btnIntroPrevious.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToWelcomeActivity() {
        rootCoordinator.navigateToWelcomeActivity(IntroActivity.this);
        finish();
    }

    private void determineChangeNextButtonToDone() {
        if(vpIntroActivity.getCurrentItem() == ConstantRegistry.INTRO_VIEW_PAGER_ONE_BEFORE_LAST_PAGE){
            btnIntroNext.setText(ConstantRegistry.DONE);
        }
    }

    @OnClick(R.id.btnIntroPrevious)
    public void previousClickListener() {
        runOnClickPreviousRoutine();
    }

    private void runOnClickPreviousRoutine() {
        determineNavigatePreviousAfterOnClick();
    }

    private void determineNavigatePreviousAfterOnClick() {
        determineNavigateToPreviousFragment();
    }

    private void determineNavigateToPreviousFragment() {
        if(isNotFirstPage()){
            determineMakePreviousButtonInvisible();
            vpIntroActivity.setCurrentItem(vpIntroActivity.getCurrentItem()-1);
        }
    }

    private boolean isNotFirstPage() {
        return (vpIntroActivity.getCurrentItem()-1) >= ConstantRegistry.INTRO_VIEW_PAGER_FIRST_PAGE;
    }

    private void determineMakePreviousButtonInvisible() {
        if(isFirstPage()){
            btnIntroPrevious.setVisibility(View.GONE);
        }
    }

    private boolean isFirstPage() {
        return (vpIntroActivity.getCurrentItem()-1) == ConstantRegistry.INTRO_VIEW_PAGER_FIRST_PAGE;
    }

    @OnClick(R.id.btnIntroSkip)
    public void skipClickListener() {
        navigateToWelcomeActivity();
    }

    // endregion

}

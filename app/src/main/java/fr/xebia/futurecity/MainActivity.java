package fr.xebia.futurecity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.metro_exit) ViewGroup view1;
    @Bind(R.id.metro_lines) ViewGroup view2;
    @Bind(R.id.line_directions) ViewGroup view3;
    @Bind(R.id.view_exits) ViewGroup view4;

    @Bind(R.id.direction_1) TextView dir1;
    @Bind(R.id.direction_2) TextView dir2;

    Animation rightToLeftEnter;
    Animation rightToLeftExit;
    Animation leftToRightEnter;
    Animation leftToRightExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rightToLeftEnter = AnimationUtils.loadAnimation(this, R.anim.right_to_left_enter);
        rightToLeftExit = AnimationUtils.loadAnimation(this, R.anim.right_to_left_exit);
        leftToRightEnter = AnimationUtils.loadAnimation(this, R.anim.left_to_right_enter);
        leftToRightExit = AnimationUtils.loadAnimation(this, R.anim.left_to_right_exit);
    }

    @OnClick(R.id.btn_line_3)
    public void onBntLine3Clicked(View view) {
        dir1.setText(getText(R.string.dir_pont_bezon));
        dir2.setText(getText(R.string.dir_gambetta));
        transitionView2ToView3();
    }

    @OnClick(R.id.btn_line_7)
    public void onBntLine7Clicked(View view) {
        dir1.setText(getText(R.string.dir_7_north));
        dir2.setText(getText(R.string.dir_7_south));
        transitionView2ToView3();
    }

    @OnClick(R.id.btn_line_8)
    public void onBntLineClicked(View view) {
        dir1.setText(getText(R.string.dir_8_balard));
        dir2.setText(getText(R.string.dir_8_creteil));
        transitionView2ToView3();
    }

    private void transitionView2ToView3() {
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.VISIBLE);
        view2.startAnimation(leftToRightExit);
        view3.startAnimation(leftToRightEnter);
    }

    @OnClick(R.id.back_to_view2)
    public void onBacktoView2Clicked() {
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.GONE);
        view2.startAnimation(rightToLeftEnter);
        view3.startAnimation(rightToLeftExit);
    }

    @OnClick(R.id.back_to_view1)
    public void onBacktoView1Clicked() {
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.GONE);
        view1.startAnimation(rightToLeftEnter);
        view2.startAnimation(rightToLeftExit);
    }

    @OnClick(R.id.back_to_view1_from_exit_view)
    public void onBacktoView1FromExitClicked() {
        view1.setVisibility(View.VISIBLE);
        view4.setVisibility(View.GONE);
        view1.startAnimation(leftToRightEnter);
        view4.startAnimation(leftToRightExit);
    }

    @OnClick(R.id.take_a_metro)
    public void onTakeMetroClicked() {
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.VISIBLE);
        view1.startAnimation(leftToRightExit);
        view2.startAnimation(leftToRightEnter);
    }

    @OnClick(R.id.get_out)
    public void onGetOutClicked() {
        view1.setVisibility(View.GONE);
        view4.setVisibility(View.VISIBLE);
        view1.startAnimation(rightToLeftExit);
        view4.startAnimation(rightToLeftEnter);
    }
}

package com.example.jeon.helper.helpList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.selectMenu;
import com.example.jeon.helper.userPage.userPageMainActivity;

import java.util.ArrayList;

import static android.view.View.GONE;
import static com.example.jeon.helper.helpList.myAskAdapter.fcmList;
import static com.example.jeon.helper.helpList.myAskAdapter.getAcceptContentNumAndUserId;
import static com.example.jeon.helper.helpList.myAskAdapter.getDinyContentNumAndUserId;

/**
 * Created by JEON on 2018-05-14.
 *
 * helpList에서 자기가 요청한 글을 누르게 되면 나타나는 다이얼로그속의  리사이클러뷰의 아이템을 뿌려주는 어댑터
 */

public class getApplyUserDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<getApplyuserData> AuD = new ArrayList<>();
    Context context;
    String key;
    String loginUserId;
    String loginUserNick;
    String title;

    int totalCount = 0; // 모집할수 있는 수
    int acceptCount = 0;

    ip ip =new ip();
    String ipad = ip.getIp();


    fcmMessagingContent fC;
    public getApplyUserDataAdapter(ArrayList<getApplyuserData> auD, Context context,String totalC,String key,String loginUserId,String loginUserNick, String title) {
        AuD = auD;
        this.context = context;
        this.totalCount = Integer.parseInt(totalC);
        this.key = key;
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
        this.title = title;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ask_apply_user_list_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // 닉네임
        ((RowCell)holder).applyUserNick.setText(AuD.get(position).userNick);

        //지역
        ((RowCell)holder).applyUserLocation.setText(AuD.get(position).location);

        //유저 점수

        int totalPoint = Integer.parseInt(AuD.get(position).givePoint)+Integer.parseInt(AuD.get(position).askPoint);
        if ( totalPoint >= 0 &&  totalPoint <10){
            //보통
            ((RowCell)holder).applyUserPoint.setText("보통");
        }else if (totalPoint >= 10){
            //우수
            ((RowCell)holder).applyUserPoint.setText("우수");
        }

        // 패널티
        ((RowCell)holder).applyUserPenalty.setText(AuD.get(position).penalty+"회");
        // 성별
        // 성별
        if( AuD.get(position).gender.equals("남자")){
            ((RowCell)holder).applyUserGender.setImageResource(R.drawable.man);
            // Glide.with(context).load(R.drawable.man).into(((RowCell) holder).gender);
        }else if ( AuD.get(position).gender.equals("여자")){
            ((RowCell)holder).applyUserGender.setImageResource(R.drawable.girl);
            //Glide.with(context).load(R.drawable.girl).into(((RowCell) holder).gender);
        }else{
            // 성별 무관 ( 디폴트 이미지 )
            ((RowCell)holder).applyUserGender.setImageResource(R.drawable.genderdefault);
            // Glide.with(context).load(R.drawable.genderdefault).into(((RowCell) holder).gender);
        }


        //프로필 사진
        if (AuD.get(position).profilePath.equals("0")){
            // 디폴트
        }else{
            if(AuD.get(position).profilePath.contains("http://k.kakaocdn.net")){
                Glide.with(context).load(AuD.get(position).profilePath).into(((RowCell) holder).applyUserProfile);
            }else{
                Glide.with(context).load(ipad+"/"+AuD.get(position).profilePath).into(((RowCell) holder).applyUserProfile);
            }

        }

        // 버튼 적용
        if (AuD.get(position).accept.equals("수락") && AuD.get(position).diny.equals("0") ){
            ((RowCell)holder).applyDiny.setVisibility(GONE);
            ((RowCell)holder).applyAccept.setVisibility(View.VISIBLE);
            ((RowCell)holder).applyAccept.setBackgroundColor(Color.GREEN);

        }else if (AuD.get(position).diny.equals("거절") && AuD.get(position).accept.equals("0") ){
            ((RowCell)holder).applyAccept.setVisibility(GONE);
            ((RowCell)holder).applyDiny.setVisibility(View.VISIBLE);
            ((RowCell)holder).applyDiny.setBackgroundColor(Color.RED);
        }else if (AuD.get(position).accept.equals("0") && AuD.get(position).diny.equals("0")){
            ((RowCell)holder).applyAccept.setVisibility(View.VISIBLE);
            ((RowCell)holder).applyAccept.setBackgroundColor(Color.parseColor("#3f51b5"));
            ((RowCell)holder).applyDiny.setVisibility(View.VISIBLE);
            ((RowCell)holder).applyDiny.setBackgroundColor(Color.parseColor("#3f51b5"));



            // 버튼 이벤트
            // 수락
            ((RowCell)holder).applyAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptBtn(holder,position);

                    //FCM 리스트에 추가
                    fC = new fcmMessagingContent(loginUserId,AuD.get(position).userId,title,"수락");
                    myAskAdapter.fcmList.add(fC);
                }
            });

            // 거절
            ((RowCell)holder).applyDiny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RowCell)holder).applyAccept.setVisibility(GONE);
                    ((RowCell)holder).applyDiny.setBackgroundColor(Color.RED);
                    dinyShardSet(key, AuD.get(position).userId);

                    //FCM 리스트에 추가
                    fC = new fcmMessagingContent(loginUserId,AuD.get(position).userId,title,"거절");
                    myAskAdapter.fcmList.add(fC);
                }
            });
        }

        // 레이아웃 클릭 유저 정보 보기
        ((RowCell)holder).applyUserListItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoShowUserDetail = new Intent(context,selectMenu.class);
                gotoShowUserDetail.putExtra("targetId",AuD.get(position).userId);
                gotoShowUserDetail.putExtra("loginUserId",loginUserId);
                ((helpListMain)context).startActivity(gotoShowUserDetail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return AuD.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder  {

        public TextView applyUserNick,applyUserLocation,applyUserPoint,applyUserPenalty;
        public ImageView applyUserProfile,applyUserGender;
        public LinearLayout applyUserListItemLayout;

        public Button applyAccept,applyDiny;

        public RowCell(View view) {
            super(view);

            applyUserNick = (TextView) view.findViewById(R.id.applyUserNick);
            applyUserLocation = (TextView) view.findViewById(R.id.applyUserLocation);
            applyUserPoint = (TextView) view.findViewById(R.id.applyUserPoint);
            applyUserPenalty = (TextView) view.findViewById(R.id.applyUserPenalty);


            applyUserProfile = (ImageView) view.findViewById(R.id.applyUserProfile);
            applyUserGender  = (ImageView) view.findViewById(R.id.applyUserGender);

            //레이아웃
            applyUserListItemLayout = (LinearLayout)view.findViewById(R.id.applyUserListItemLayout);

            // 버튼
            applyAccept =  (Button)view.findViewById(R.id.applyAccept);
            applyDiny =  (Button)view.findViewById(R.id.applyDiny);

        }
    }

    // 수락 버튼 이벤트
    public void acceptBtn( RecyclerView.ViewHolder holder,int position){
        if(AuD.get(position).choice == false){
            acceptCount = acceptCount+1;
            if ( totalCount < acceptCount){
                Toast.makeText(context, "모집 인원을 초과했습니다.", Toast.LENGTH_SHORT).show();
            }else{
                // 수락 버튼의 배경색을 바꿀것
                ((RowCell)holder).applyDiny.setVisibility(GONE);
                ((RowCell)holder).applyAccept.setBackgroundColor(Color.GREEN);

                AuD.get(position).choice = true;

                // static 으로 있는 녀석에 저장
                acceptShardSet(key,AuD.get(position).userId);
            }
        }else{
            Toast.makeText(context, "이미 선택했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //  수락한 사람 저장
    public void acceptShardSet(String key,String applyId){
        // 필요한게 게시글 번호와, 지원자 아이디
        if (TextUtils.isEmpty(getAcceptContentNumAndUserId)){
            // 비어있다면
            getAcceptContentNumAndUserId = key+"!"+applyId;
        }else{
            getAcceptContentNumAndUserId = getAcceptContentNumAndUserId+"@"+applyId;
        }




    }

    public void dinyShardSet(String key,String applyId){
        // 필요한게 게시글 번호와, 지원자 아이디
        if ( TextUtils.isEmpty(getDinyContentNumAndUserId)){
            getDinyContentNumAndUserId = key+"!"+applyId;
        }else{
            getDinyContentNumAndUserId = getDinyContentNumAndUserId+"@"+applyId;
        }

    }


}

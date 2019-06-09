package com.example.hp.tiptip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private String[] mContactIds; //联系人ID字符串数组
    private String[] mContactNames; //联系人昵称字符串数组
    private List<String> mContactList; // 联系人IDList（转换成拼音）
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactAdapter(Context context,  String[] contactIds, String[] contactNames) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mContactIds = contactIds;
        mContactNames = contactNames;
        handleContact();
    }

    private void handleContact() {
        mContactList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();  //拼音和ID的对应
        Map<String, String> map2 = new HashMap<>();  //拼音和昵称的对应

        for (int i = 0; i < mContactIds.length; i++) {
            String pinyin = Utils.getPingYin(mContactIds[i]);
            map.put(pinyin, mContactIds[i]);
            map2.put(pinyin,mContactNames[i]);
            mContactList.add(pinyin);
        }
        Collections.sort(mContactList, new ContactComparator());

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactList.size(); i++) {
            String id_pinyin = mContactList.get(i);
            String character = (id_pinyin.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }

            resultList.add(new Contact(map.get(id_pinyin), map2.get(id_pinyin),ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getmId());
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(resultList.get(position).getmId());
            ((ContactHolder) holder).mTextView2.setText(resultList.get(position).getmName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resultList.get(position).getmType();
    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);

            mTextView =  view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mTextView2;
        ContactHolder(View view) {
            super(view);
            mTextView =  view.findViewById(R.id.contact_id);
            mTextView2 = view.findViewById(R.id.contact_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,Friend_infoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("friendId",mTextView.getText().toString());
                    intent.putExtra("bundle",bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getmId().equals(character)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }
}

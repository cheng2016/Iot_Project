package com.cds.iot.data.source.local;

import android.content.Context;
import android.util.Log;

import com.cds.iot.data.entity.SMessage;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class SMessageDaoUtils {
    private static final String TAG = "SMessageDaoUtils";
    private DaoManager mManager;

    public SMessageDaoUtils(Context context) {
        this.mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成SMessage记录的插入，如果表未创建，先创建SMessage表
     *
     * @param SMessage
     * @return
     */
    public boolean insert(SMessage SMessage) {
        boolean flag = false;
        flag = mManager.getDaoSession().getSMessageDao().insert(SMessage) == -1 ? false : true;
        Log.i(TAG, "insert SMessage :" + flag + "-->" + SMessage.toString());
        return flag;
    }


    /**
     * 插入多条数据，在子线程操作
     *
     * @param SMessageList
     * @return
     */
    public boolean insert(final List<SMessage> SMessageList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (SMessage SMessage : SMessageList) {
                        mManager.getDaoSession().insertOrReplace(SMessage);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param SMessage
     * @return
     */
    public boolean updateSMessage(SMessage SMessage) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(SMessage);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param SMessage
     * @return
     */
    public boolean deleteSMessage(SMessage SMessage) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(SMessage);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean deleteSMessage(final List<SMessage> SMessageList) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (SMessage SMessage : SMessageList) {
                        mManager.getDaoSession().delete(SMessage);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(SMessage.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<SMessage> queryAllSMessage() {
        return mManager.getDaoSession().loadAll(SMessage.class);
    }

    public List<SMessage> querySMessage(int limit) {
        QueryBuilder<SMessage> qb = mManager.getDaoSession().queryBuilder(SMessage.class).limit(limit);
        return qb.list();
    }

    /**
     * 根据条件查询
     *
     * @param where
     * @param conditions
     * @return
     */
    public List<SMessage> querySMessage(String where, Object[] conditions) {
        QueryBuilder<SMessage> qb = mManager.getDaoSession().queryBuilder(SMessage.class)
                .where(new WhereCondition.StringCondition(where, conditions));
        return qb.list();
    }

    /**
     * 根据条件查询并限制条数
     *
     * @param where
     * @param limit
     * @param conditions
     * @return
     */
    public List<SMessage> querySMessage(String where, Object[] conditions, int limit) {
        QueryBuilder<SMessage> qb = mManager.getDaoSession().queryBuilder(SMessage.class)
                .where(new WhereCondition.StringCondition(where, conditions))
                .limit(limit);
        return qb.list();
    }

    /**
     * 分页条件查询实体集合
     *
     * @param where
     * @param conditions
     * @param properties 倒序
     * @param offset
     * @param limit
     * @return
     */
    public List<SMessage> querySMessage(String where, Object[] conditions, Property properties, int offset, int limit) {
        QueryBuilder<SMessage> qb = mManager.getDaoSession().queryBuilder(SMessage.class)
                .orderDesc(properties)
                .where(new WhereCondition.StringCondition(where, conditions))
                .offset(offset * limit)
                .limit(limit);
        return qb.list();
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public SMessage querySMessageById(long key) {
        return mManager.getDaoSession().load(SMessage.class, key);
    }


    /**
     * 使用native sql进行查询操作
     */
    public List<SMessage> queryByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(SMessage.class, sql, conditions);
    }
}

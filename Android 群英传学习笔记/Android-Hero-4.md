---
title: Android-群英传读书笔记（4）
date: 2016-01-25 15:26:58
tags: android
categories: About Java
---

Android ListView相关解析

<!-- more -->



### 4.1 ListView常用优化技巧

4.1.1 使用 ViewHolder 模式提高效率

ViewHolder 模式是提高其效率的很重要的一个方法。充分利用了 ListView 的视图缓存机制，避免了每次调用 getView() 时候都去通过 findviewbyid()实例化控件，使用 ViewHolder 模式非常简单，只需要在自定义 Adapter 中，自定义一个内部类 ViewHolder，并将布局中的控件作为成员变量。注意：recyclerView 不就是内置了 ViewHolder吗。

	public final class ViewHolder{
		public ImageView img;
		public TextView title;
	} 

接下来，只要在 getView()方法中通过视图缓存机制来重用缓存即可，完整使用 ViewHolder 创建 ListViewAdapter 的代码如下：

	public class ViewHolderAdapter extends BaseAdapter{
		private list<String> mData;
		private LayoutInflate mInflater;
		
	public ViewHolderAdapter(Context context,List<String> data){
		this.mData = mData;
		mInflater = layoutInflater.from(context);
	}
	@override
	public int getCount(){
		return mData.size();
	}
	@override
	public Object getItem(int position){
		return mData.get(position);
	}
	@override
	public long getItem(int postion){
		return positon;
	}

	@override
	public View getView(int position,View convertView,ViewGroup parent){
		ViewHolder holder = null;
		//判断是否缓存
		if(convertView == null){
		holder = new ViewHolder();
		//通过LayoutInflater 实例化布局
		convertView = mInflater.inflate(R.layout.viewholder_item,null);
		holder.img=(ImageView)convertView.findViewById();
		holder.title=()convertView.findViewById();
		convertView.setTag(holder);
	}else{
		//通过tag找到缓存布局
		holder=(ViewHolder)convertView.getTag();
	}
		//设置布局中控件要显示的视图
		holder.img.setBackgroudResourse(R.drawerable.ic_launcherr);
		holder.title.setText(mData.get(Position));
		return convertView;
	}
	public final class ViewHolder{
		public ImageView img;
		public TextView title;
	}
		
	}


4.1.2 设置项目间分割线

同样很简单，代码如下：

	android:divider="@android:color/darker_gray"
	android:dividerHeight="10dp"

4.1.3 隐藏 ListView 滚动条

很简单，就是不要再 ListView 左右滑动时候，有滚动条出现
	
	android:scrollbars = "none"
 
4.1.4 取消 ListView 点击效果

点击 Item 会有一个背景颜色效果，5.x 后为波纹，之前为背景颜色，取消或设置无

	android:listSelector="#000000000"
或者
	android:listSelector="@android:color/transparent"

4.1.5 设置 ListView 需要显示在第几页

4.1.6 动态修改 ListView 

	ListView 的数据某些情况下是要变化的，可以重新设置 ListView 的 Adapter 来更新 ListView 的显示，但这也是重新获取一下数据，相当于重新创建 ListView，非常不友好。

可以动态修改，代码如下所示：

	mData.add("new");
	mAdapter.notifyDataSetChanged();

4.1.7 遍历 ListView 中所有的 Item

ListView 作为一个 ViewGroup ，为我们提供了操作子 View 的各种办法，常用 getChildAt()来获取第i个子 View，代码如下：

	for(int i= 0;i < mListView.getChildCount();i ++){
		View view = mListView.getChildAt(i);	
	}

4.1.8 处理空的 ListView

4.1.9 ListView滑动监听

4.1.9.1 OnTouchListener

4.1.9.2 OnScrollListener

先上代码：

	mListView.setOnScrollListener(new OnScrollListener(){
	@override
	public void onScrollStateChanged(AbsListView view,int scrollState){
		switch(scrollState){
		case OnScrollListener.SCROLL_STATE_IDLE:
			//滑动停止
			break；
		case onScrollListener.SCROLL_STATE_TOUCH_SCROLL;
			//正在滚动
			break；
		case onScrollListener.SCROLL_STATE_FLING;
			//手指用力滑动时，在离开ListView，由于惯性还在滚动
			break；
		}
	}
	@override
	public void onScroll(AbsListView,int firstVisibleItem,int visibleItemCount,int totalCount){
			//滑动时一直调用
		}
	});

当用户没有做用力滑动时，即不会产生惯性滑动时，这个方法会回调2次，否则3次。
而 onScroll() 在 ListView 滑动时，会一直调用，而三个参数精确的显示了 ListView 当前的状态。分别为：第一个能看见的item的id，当前能看见的item总数，总的Item数。

通过上面这些参数可以很方便的判断 ListView 当前的状态，比如当前是否滑动到了最后一行：

	if(firstVisibleItem + VisibleItemCount==totalItemCount && totalCountItem > 0){
		//滑动到最后一行
	}
	
判断当前滑动方向：
	
	if(firstVisibleItem>lastVisiableItemPosition){
		//上滑
	}else{
		//下滑
	}
	lastVisibleItemPosition=firstVisibleItem;

### 4.2 ListView常用扩展

4.2.1 具有弹性的 ListView

4.2.2 自动显示，影藏布局的 ListView

滑动影藏标题栏，判断滑动方向。做一些初始化工作，避免第一个 Item 被 Toolbar遮挡，代码如下所示：

	View header = new View(this);
	header.setLayoutPararms(new AbsListView.LayoutParaams(
		AbsListView.LayoutParams.MATCH_PARENT,
		(int)getResources().getDimension(R.dimen.abc_action_bar_default_height_material)));//系统actionbar高度
	mListView.addHeaderView(header);

另外定义一个 mTouchslop 来获取系统认为的最低滑动距离：

	mTouchSLop = ViewConfiguration.get(this).getScaledTouchSlop();

现在判断滑动事件：

	View.OnTouchListener myTouchListener = new View.OnTOuchLisener(){
		@override
		public boolean onTouch(View v,MotionEvent ev){
		switch(ec.getAction()){
			case MotionEvent.ACTION_DOWN:
					mFirstY = event.getY();
					break;
			case MotionEvent.ACTION_MOVE:
					mCurrentY = event.getY();
					if(mCurrentY-mFirstY>mTouchSlop){
						direction=0;//down	
					}else{
						direction=1;//up
					}
					if(direction=1){
						if(mShow){
							toolbarAnim(0);//hide
							mShow=!mShow;
					}
					else if(direction=0){
						if(!mShow){
							toolbarAnim(1);//show
							mShow=!mShow;		
					}
				}
				break;
			case MotionEvent.ACTION_UP:
					
					break;
			}
		}
			return false；
	};

最后加上控制布局影藏的动画效果如下,简单的位移属性动画：

	private void toolbarAnim(int flag){
		if(mAnimator!=null&&mAnimator.isRunning){
				mAminator.cancel();
		}
		if(flag=0){
			mAnimator=objectAnimator.ofFloat(mToolbar,"translateY",mToolbar.getTranslateY(),0);
		}else{
			mAnimator=objectAnimator.ofFloat(mToolbar,"translateY",mToolbar.getTranslateY(),-mToolbar.getHeight());
		}
		mAnimator.start();
	}


4.2.3 聊天 ListView

4.2.4 动态改变 ListView 布局


### 扩充阅读

[郭霖ListView](http://blog.csdn.net/guolin_blog/article/details/44996879)
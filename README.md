## 具备下拉刷新和上拉加载更多的`RecyclerView`
具体介绍请看：[传送门](https://github.com/oynix/android-learn/blob/master/widget/xRecyclerView/xRecyclerView.md)

最新版本：2.0.0

### 使用
1. 根目录`build.gradle`添加
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. 项目目录`build.gradle`添加
```groovy
dependencies {
    implementation 'com.github.oynix:wraprecyclerview:2.0.0'
}
```
3. Sync，即可。[详细说明](https://github.com/oynix/android-learn/blob/master/widget/xRecyclerView/xRecyclerView.md)

-------------------------------

#### 2020.02.08
- v2.0.0
调整包名

#### 2020.02.07
- v1.0.0
修复已知问题，增加自定义LoadMoreView，发布至`JitPack`
- v1.0.1
修复滑动距离不足导致循环滑动的bug

#### 2019.12.18更新
- 修复bug：列表项未占满列表时，滑动列表会循环触发‘加载更多’操作

#### 2018.10.25更新

将原WrapperRecyclerView进行了重写：
1. 思路大致相同
2. 将更多内容封装
3. 修复了部分已知问题

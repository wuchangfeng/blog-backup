---
title: 设计模式之状态模式
date: 2016-05-02 18:52:27
tags: design-pattern
categories: About Java
---

通常的我们喜欢利用 if... else if... else 或者 switch...case...来进行状态的切换，在状态类型较少或者简单的情况下这样可以达到一些我们想要的目的，但是一旦状态判断复杂或者增加了新的状态，这样的判断就显得非常不好了,明显违背了设计模式的原则-封装和开闭原则。 

较多情况下**对象往往会根据自身的状态来决定自身表现的行为**，这些状态都在对象内部定义好了的。


### 一 . 资料阅读

先可以看这个人写的状态模式入门引入，非常好。
[Java设计模式之状态模式](http://blog.csdn.net/jason0539/article/details/45021055)

### 二 . 场景引入

下面这个游戏，简单明了的讲解了状态模式

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E7%8A%B6%E6%80%81%E6%A8%A1%E5%BC%8F2.jpg)


### 三 . UML图示

![](http://7xrl8j.com1.z0.glb.clouddn.com/UML_state.jpg)

### 四 . 实例展示


首先有一个糖果机类，有四种状态：
``` java
public class GumballMachine {
 
	final static int SOLD_OUT = 0;
	final static int NO_QUARTER = 1;
	final static int HAS_QUARTER = 2;
	final static int SOLD = 3;
 
	int state = SOLD_OUT;
	int count = 0;
  
	public GumballMachine(int count) {
		this.count = count;
		if (count > 0) {
			state = NO_QUARTER;
		}
	}

	/**
	 * 投币25分钱
	 */
	public void insertQuarter() {
		if (state == HAS_QUARTER) {
			System.out.println("You can't insert another quarter");
		} else if (state == NO_QUARTER) {
			state = HAS_QUARTER;
			System.out.println("You inserted a quarter");
		} else if (state == SOLD_OUT) {
			System.out.println("You can't insert a quarter, the machine is sold out");
		} else if (state == SOLD) {
        	System.out.println("Please wait, we're already giving you a gumball");
		}
	}

	/**
	 * 退币，25分退出
	 */
	public void ejectQuarter() {
		if (state == HAS_QUARTER) {
			System.out.println("Quarter returned");
			state = NO_QUARTER;
		} else if (state == NO_QUARTER) {
			System.out.println("You haven't inserted a quarter");
		} else if (state == SOLD) {
			System.out.println("Sorry, you already turned the crank");
		} else if (state == SOLD_OUT) {
        	System.out.println("You can't eject, you haven't inserted a quarter yet");
		}
	}

    /**
	 * 转动转轴
	 */
	public void turnCrank() {
		if (state == SOLD) {
			System.out.println("Turning twice doesn't get you another gumball!");
		} else if (state == NO_QUARTER) {
			System.out.println("You turned but there's no quarter");
		} else if (state == SOLD_OUT) {
			System.out.println("You turned, but there are no gumballs");
		} else if (state == HAS_QUARTER) {
			System.out.println("You turned...");
			state = SOLD;
			dispense();
		}
	}

	/**
	 * 分发糖果
	 */
	public void dispense() {
		if (state == SOLD) {
			System.out.println("A gumball comes rolling out the slot");
			count = count - 1;
			if (count == 0) {
				System.out.println("Oops, out of gumballs!");
				state = SOLD_OUT;
			} else {
				state = NO_QUARTER;
			}
		} else if (state == NO_QUARTER) {
			System.out.println("You need to pay first");
		} else if (state == SOLD_OUT) {
			System.out.println("No gumball dispensed");
		} else if (state == HAS_QUARTER) {
			System.out.println("No gumball dispensed");
		}
	}
 	
	// 更新糖果机内糖果的数目，并重置糖果机状态
	public void refill(int numGumBalls) {
		this.count = numGumBalls;
		state = NO_QUARTER;
	}
}
```

### 五. 更新要求

糖果公司 CEO 希望加上一种抽奖程序，即转动转轴有 10% 的概率得到两颗糖。

所以以下问题是必须要考虑到的：

* 加上一个**赢家的状态**
* 在四个方法里面要加上判断当前状态是否为赢家的状态
* turnCarnk()会变得更为糟糕，要加上代码检查当前顾客是否为赢家，然后再切换到赢家状态或者售出糖果状态

而考虑到以上，我们应该做的就是试着局部化每个状态的行为，这样我们如果针对某个状态做了改变，就不会影响其他代码了。简单点说就是将每个状态的行为都放在各自的类中，每个状态只要实现自己的动作就可以了

### 六 . 代码升级

基于上述分析，可以有以下几个步骤：

* 定义 state 接口，在这个接口内，糖果机的每个动作都有一个对应的方法
* 为每个状态类实现状态接口
* 将动作委托到状态类

状态接口的定义

``` java
public interface State {
	public void insertQuarter();
	public void ejectQuarter();
	public void turnCrank();
	public void dispense();
}
```

其中一个状态的实现(已经有硬币在糖果机里面了)

``` java
public class HasQuarterState implements State {
	GumballMachine gumballMachine;
 
	public HasQuarterState(GumballMachine gumballMachine) {
		this.gumballMachine = gumballMachine;
	}
  
	public void insertQuarter() {
		System.out.println("You can't insert another quarter");
	}
 
	public void ejectQuarter() {
		System.out.println("Quarter returned");
		// 这个状态的改变，会导致糖果机行为的改变
		gumballMachine.setState(gumballMachine.getNoQuarterState());
	}
 
	public void turnCrank() {
		System.out.println("You turned...");
		gumballMachine.setState(gumballMachine.getSoldState());
	}

    public void dispense() {
        System.out.println("No gumball dispensed");
    }
 
	public String toString() {
		return "waiting for turn of crank";
	}
}	
```

省略了其他几种状态，当然了赢家状态还是要写上的

``` java
public class WinnerState implements State {
    GumballMachine gumballMachine;
 
    public WinnerState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }
 
	public void insertQuarter() {
		System.out.println("Please wait, we're already giving you a Gumball");
	}
 
	public void ejectQuarter() {
		System.out.println("Please wait, we're already giving you a Gumball");
	}
 
	public void turnCrank() {
		System.out.println("Turning again doesn't get you another gumball!");
	}
 
	public void dispense() {
		System.out.println("YOU'RE A WINNER! You get two gumballs for your quarter");
		gumballMachine.releaseBall();
		if (gumballMachine.getCount() == 0) {
			// 状态的改变
			gumballMachine.setState(gumballMachine.getSoldOutState());
		} else {
			gumballMachine.releaseBall();
			if (gumballMachine.getCount() > 0) {
				// 状态的改变
				gumballMachine.setState(gumballMachine.getNoQuarterState());
			} else {
            	System.out.println("Oops, out of gumballs!");
				gumballMachine.setState(gumballMachine.getSoldOutState());
			}
		}
	}
 
	public String toString() {
		return "despensing two gumballs for your quarter, because YOU'RE A WINNER!";
	}
}
```

完整的糖果机类

``` java
public class GumballMachine {
 
	State soldOutState;
	State noQuarterState;
	State hasQuarterState;
	State soldState;
	State winnerState;
 	// 初始的状态
	State state = soldOutState;
	int count = 0;
 
	public GumballMachine(int numberGumballs) {
		soldOutState = new SoldOutState(this);
		noQuarterState = new NoQuarterState(this);
		hasQuarterState = new HasQuarterState(this);
		soldState = new SoldState(this);
		winnerState = new WinnerState(this);

		this.count = numberGumballs;
		// 状态的改变，糖果数目超过 0 改为 noQuarterState 状态
 		if (numberGumballs > 0) {
			state = noQuarterState;
		} 
	}
 
	public void insertQuarter() {
		// 状态的委托
		state.insertQuarter();
	}
 
	public void ejectQuarter() {
		state.ejectQuarter();
	}
 
	public void turnCrank() {
		state.turnCrank();
		state.dispense();
	}

	void setState(State state) {
		this.state = state;
	}
 
	void releaseBall() {
		System.out.println("A gumball comes rolling out the slot...");
		if (count != 0) {
			count = count - 1;
		}
	}
 
	int getCount() {
		return count;
	}
 
	void refill(int count) {
		this.count = count;
		state = noQuarterState;
	}

    public State getState() {
        return state;
    }

    public State getSoldOutState() {
        return soldOutState;
    }

    public State getNoQuarterState() {
        return noQuarterState;
    }

    public State getHasQuarterState() {
        return hasQuarterState;
    }

    public State getSoldState() {
        return soldState;
    }

    public State getWinnerState() {
        return winnerState;
    }
 
	public String toString() {
	  //....
	}
}
```

测试程序
``` java
public class GumballMachineTestDrive {

	public static void main(String[] args) {
		GumballMachine gumballMachine = 
			new GumballMachine(10);

		System.out.println(gumballMachine);
		// 显示效果一
		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();
		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();

		System.out.println(gumballMachine);
		// 显示效果二
		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();
		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();

		System.out.println(gumballMachine);

		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();
		gumballMachine.insertQuarter();
		gumballMachine.turnCrank();

		System.out.println(gumballMachine);
	}
}
```

### 七 . 状态模式的优缺点

**状态模式**：允许对象在内部状态改变时改变它的行为，对象看起来好像修改了它的类。
这个模式将状态封装成为独立的类，并将动作委托到代表当前状态的对象，我们知道行为会随着内部状态的改变而改变。糖果机提供了一个很好的例子：当糖果机是在 NoQuarterState 和 HasQuarterState 两种不同状态时，你投入 25 分钱，就会得到不同的行为。并且我们通过使用组合

**一个类，一个责任**：之所以不让 SoldState 直接发放两颗糖果，而设置 WinnerState ，是因为完全遵守一个类一个责任，假设将发放两颗糖果的代码放在 SoldState 中，确实可以解决当前问题，但是一旦促销方案改变或者促销结束，如果要做一些改动是非常麻烦的。

### 八 . 状态模式与策略模式比较

* 以状态模式而言，将一群行为封装在状态对象中，context 的行为可以随时委托到那些状态对象中的一个。随着时间的流式，当前状态在状态对象集合中游走改变，以反映出 context 内部的状态，因此 context 状态也会跟着改变，而客户不知道这一切。

* 策略模式而言，客户通常主动指定 Context 所要组合的策略对象是哪一个，虽然策略模式让我们具有弹性，能够在运行时改变策略，但是对于某个 context 对象来说，通常只有一个最适当的策略对象。策略模式一般是除了继承之外一种弹性替代方案，**如果使用继承定义了一个类的行为，将会被这个行为困住，甚至修改都很难**，有了策略模式，可以通过组合不同的对象来改变行为。即将可以互换的行为封装起来，使用委托的办法，决定使用哪一个行为。
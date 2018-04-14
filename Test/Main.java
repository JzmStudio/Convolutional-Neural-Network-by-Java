package Test;
import Networks.*;
/**
 * 所有类注释中，'前向'为计算结果的过程，'后向'为计算误差的过程
 * @author Administrator
 *
 */
public class Main {
	
	static double speed=0.3f;
	public static void main(String args[])
	{
		long startTime = System.currentTimeMillis();    //获取开始时间

		int i;
		InvertImage image=new InvertImage("F:/ML/exam/heiban/ (1).jpg");	//本地某测试样例所在路径
		/*检测是否有之前学习过的权值保存*/
		/*若有则继续使用，无则创建*/
		SaveWB[] s1=new SaveWB[5];
		for(i=0;i<5;i++)
		{
			s1[i]=new SaveWB("F:/ML/floor1"+i+".wb");
		}
		SaveWB[] s3=new SaveWB[10];
		for(i=0;i<10;i++)
		{
			s3[i]=new SaveWB("F:/ML/floor3"+i+".wb");
		}
		SaveWB[] s5=new SaveWB[3];
		for(i=0;i<3;i++)
		{
			s5[i]=new SaveWB("F:/ML/floor5"+i+".wb");
		}
		
		/*初始化*/
		/*第一层卷积*/
		Filter3[] f1=new Filter3[5];	//第一层神经元
		if(!s1[0].isExisted())
		{
			Kernel3[] k1 = new Kernel3[5];	//第一层卷积核
			double[] b1=new double[5];
			for(i=0;i<5;i++)
			{
				k1[i]=new Kernel3(5);
			}
			for(i=0;i<5;i++)
			{
				f1[i]=new Filter3(image, image.getlength(), 5, k1[i].getkernel(), b1[i]);
			}
		}
		else	//若之前计算过，有权值保存
		{
			double[][][] k1=new double[3][5][5];
			double[] b1=new double[5];
			for(i=0;i<5;i++)
			{
				b1[i]=s1[i].readWB3(k1);
				f1[i]=new Filter3(image, image.getlength(), 5, k1, b1[i]);
			}
		}
		double[][][] r1=new double[5][][];
		double[][][] e1;
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		
		//CNNMath.printKernel(r1[0]);
		/*第二层池化*/
		double[][][] e2;	//本层误差
		double[][][] r2=new double[5][][];
		Pool[] pool1=new Pool[5];
		for(i=0;i<5;i++)
		{
			pool1[i]=new Pool(r1[i], null);
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		e2=new double[5][r2[0].length][r2[0][0].length];
		//CNNMath.printKernel(r2[0]);
		/*第三层卷积*/
		double[][][] r3=new double[10][][];
		Filter[] f3=new Filter[10];
		double[][][] k2;
		double[] b2;
		k2=new double[10][][];
		b2=new double[10];
		for(i=0;i<10;i++)
		{
			k2[i]=KernelBuilder.newKernel(5);
		}
		//若有权值保存
		if(s3[0].isExisted())
		{
			for(i=0;i<10;i++)
			{
				b2[i]=s3[i].readWB2(k2[i]);
			}
		}
		
		for(i=0;i<10;i++)
		{
			f3[i]=new Filter(3, r2[0].length, 5, k2[i], b2[i]);
		}
		
		/*设置与前一层某些输入相连接*/
		addInToFilter(f3[0], r2, e2, 0, 1, 2);
		addInToFilter(f3[1], r2, e2, 0, 1, 3);
		addInToFilter(f3[2], r2, e2, 0, 1, 4);
		addInToFilter(f3[3], r2, e2, 0, 2, 3);
		addInToFilter(f3[4], r2, e2, 0, 2, 4);
		addInToFilter(f3[5], r2, e2, 0, 3, 4);
		addInToFilter(f3[6], r2, e2, 1, 2, 3);
		addInToFilter(f3[7], r2, e2, 1, 2, 4);
		addInToFilter(f3[8], r2, e2, 1, 3, 4);
		addInToFilter(f3[9], r2, e2, 2, 3, 4);
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		/*第四层池化*/
		double r4[][][]=new double[r3.length][][];
		double e4[][][]=new double[r3.length][][];
		Pool[] pool2=new Pool[10];
		for(i=0;i<10;i++)
		{
			pool2[i]=new Pool(r3[i], null);
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		
		/*第五层全连接*/
		double[] r5=new double[3];	//0为黑板概率，1为人物概率，2为其它概率
		double[] e5=new double[3];
		double[][][][] k5=new double[3][][][];	//链接不同输出单元的权值
		FullJoin[] full=new FullJoin[3];
		double[] b5=new double[3];
		for(i=0;i<3;i++)
		{
			k5[i]=KernelBuilder.newKernel3(r4.length, r4[0].length);
		}
		if(s5[0].isExisted())
		{
			for(i=0;i<3;i++)
			{
				b5[i]=s5[i].readWB3(k5[i]);
			}
		}
		for(i=0;i<3;i++)
		{
			full[i]=new FullJoin(r4[0].length, r4.length,k5[i],b5[i]);
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		System.out.println("----------------初始化完成------------------");
		
		for(int k=0;k<20;k++)
		{
		
			System.out.println("学习黑板图片");
			for(int j=1;j<7;j++)
			{
				image.changeImage("F:/ML/training/heiban/ ("+j+").jpg");
				/*前向计算*/
				for(i=0;i<5;i++)
				{
					r1[i]=f1[i].filter3();
				}
				for(i=0;i<5;i++)
				{
					r2[i]=pool1[i].sample();
				}
				for(i=0;i<10;i++)
				{
					r3[i]=f3[i].filter();
				}
				for(i=0;i<10;i++)
				{
					r4[i]=pool2[i].sample();
				}
				for(i=0;i<3;i++)
				{
					r5[i]=full[i].CalculateFullJoin(r4);
				}
				image.printPath();
				System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
				/*后向更新权值*/
				for(i=0;i<3;i++)
				{
					k5[i]=full[i].getWeight();
				}
				e5[0]=full[0].updateWB(0.9f);
				e5[1]=full[1].updateWB(0.1f);
				e5[2]=full[2].updateWB(0.1f);
				System.out.println("************5***************");
				System.out.println(""+e5[0]+" "+e5[1]+" "+e5[2]);
				e4=Pool.error(k5, e5, r4);	//计算第4层池化层输出误差
				clearToZero(e2);	//由于第三层持有第二层误差的引用不能变化(不能new新的矩阵)，所以需要先将原来的误差结果清零！
				for(i=0;i<10;i++)
				{
					f3[i].updateWB(e4[i]);
					f3[i].updateBackError();
				}
				e1=Filter.error(e2, r1);
				for(i=0;i<5;i++)
				{
					f1[i].updateWB(e1[i]);
				}
			
				System.out.println("\n学习人物图片");
				/*前向计算*/
				//image.changeImage("F:/ML/training/renwu/ ("+j+").jpg");
				image.changeImage("F:/ML/training/renwu/ (1).jpg");
				//f1[0].printR();
				for(i=0;i<5;i++)
				{
					r1[i]=f1[i].filter3();
				}
				for(i=0;i<5;i++)
				{
					r2[i]=pool1[i].sample();
				}
				for(i=0;i<10;i++)
				{
					r3[i]=f3[i].filter();
				}
				for(i=0;i<10;i++)
				{
					r4[i]=pool2[i].sample();
				}
				for(i=0;i<3;i++)
				{
					r5[i]=full[i].CalculateFullJoin(r4);
				}
				image.printPath();
				System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
				/*后向更新权值*/
				for(i=0;i<3;i++)
				{
					k5[i]=full[i].getWeight();
				}
				e5[0]=full[0].updateWB(0.1f);
				e5[1]=full[1].updateWB(0.9f);
				e5[2]=full[2].updateWB(0.1f);
				System.out.println("----------------Error 5-----------------");
				System.out.println(""+e5[0]+" "+e5[1]+" "+e5[2]);
				e4=Pool.error(k5, e5, r4);	//计算第4层池化层输出误差
				clearToZero(e2);	//由于第三层持有第二层误差的引用不能变化(不能new新的矩阵)，所以需要先将原来的误差结果清零！
				for(i=0;i<10;i++)
				{
					f3[i].updateWB(e4[i]);
					f3[i].updateBackError();
				}
				e1=Filter.error(e2, r1);
				for(i=0;i<5;i++)
				{
					f1[i].updateWB(e1[i]);
				}
			
				System.out.println("学习演唱会图片");
				/*前向计算*/
				image.changeImage("F:/ML/training/wutai/ ("+j+").jpg");
				for(i=0;i<5;i++)
				{
					r1[i]=f1[i].filter3();
				}
				//System.out.println("-----------1-------------");
				//CNNMath.printKernel(r1[0]);
				for(i=0;i<5;i++)
				{
					r2[i]=pool1[i].sample();
				}
				//System.out.println("---------------2-------------");
				//CNNMath.printKernel(r2[0]);
				for(i=0;i<10;i++)
				{
					r3[i]=f3[i].filter();
				}
				//System.out.println("---------------3-------------");
				//CNNMath.printKernel(r3[0]);
				for(i=0;i<10;i++)
				{
					r4[i]=pool2[i].sample();
				}
				//System.out.println("---------------4-------------");
				//CNNMath.printKernel(r4[0]);
				for(i=0;i<3;i++)
				{
					r5[i]=full[i].CalculateFullJoin(r4);
				}
				System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
				/*后向更新权值*/
				for(i=0;i<3;i++)
				{
					k5[i]=full[i].getWeight();
				}
				e5[0]=full[0].updateWB(0.1f);
				e5[1]=full[1].updateWB(0.1f);
				e5[2]=full[2].updateWB(0.9f);
				System.out.println("************Error 5***************");
				System.out.println(""+e5[0]+" "+e5[1]+" "+e5[2]);
				e4=Pool.error(k5, e5, r4);	//计算第4层池化层输出误差
				clearToZero(e2);	//由于第三层持有第二层误差的引用不能变化(不能new新的矩阵)，所以需要先将原来的误差结果清零！
				for(i=0;i<10;i++)
				{
					f3[i].updateWB(e4[i]);
					f3[i].updateBackError();
				}
				e1=Filter.error(e2, r1);
				for(i=0;i<5;i++)
				{
					f1[i].updateWB(e1[i]);
				}
			}
		
		}
		
		/*以下为此次学习完进行的几个测试*/
		System.out.println("\n测试");
		/*前向计算*/
		image.changeImage("F:/ML/training/heiban/ (3).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		/*前向计算*/
		image.changeImage("F:/ML/training/renwu/ (16).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		/*前向计算*/
		image.changeImage("F:/ML/training/wutai/ (24).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		/*前向计算*/
		image.changeImage("F:/ML/exam/heiban/ (3).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		/*前向计算*/
		image.changeImage("F:/ML/exam/renwu/ (3).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		/*前向计算*/
		image.changeImage("F:/ML/exam/wutai/ (4).jpg");
		for(i=0;i<5;i++)
		{
			r1[i]=f1[i].filter3();
		}
		for(i=0;i<5;i++)
		{
			r2[i]=pool1[i].sample();
		}
		for(i=0;i<10;i++)
		{
			r3[i]=f3[i].filter();
		}
		for(i=0;i<10;i++)
		{
			r4[i]=pool2[i].sample();
		}
		for(i=0;i<3;i++)
		{
			r5[i]=full[i].CalculateFullJoin(r4);
		}
		System.out.println("黑板"+r5[0]+"人物"+r5[1]+"演唱会"+r5[2]);
		
		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		System.out.println("Write weight and bias in file");
		
		for(i=0;i<5;i++)
		{
			f1[i].writeWBInFile(s1[i]);
		}
		for(i=0;i<10;i++)
		{
			f3[i].writeWBInFile(s3[i]);
		}
		for(i=0;i<3;i++)
		{
			full[i].writeWBInFile(s5[i]);
		}
		
		System.out.println("Write done");
	}
	
	private static void addInToFilter(Filter filter,double[][][] in,double[][][] error,int... index)
	{
		for(int i:index)
		{
			filter.addInput(in[i],error[i]);
		}
	}
	
	private static void clearToZero(double[][][] src)
	{
		int i,j,k;
		for(k=0;k<src.length;k++)
		{
			for(i=0;i<src[k].length;i++)
			{
				for(j=0;j<src[k][i].length;j++)
				{
					src[k][i][j]=0;
				}
			}
		}
	}
}

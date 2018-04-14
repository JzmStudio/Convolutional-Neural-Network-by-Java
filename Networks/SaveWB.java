package Networks;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class SaveWB {
	File f;
	byte flag=0;	//flag==0 is having a file existed;flag==1 is not
	
	public SaveWB(String path)
	{
		f=new File(path);
		if(!f.exists())
		{
			flag=1;
			try {
				f.createNewFile();
				System.out.println("Create");
			} catch (IOException e) {
				System.out.println("Open fail");
				e.printStackTrace();
			}
		}
		else
		{
			flag=0;
			System.out.println("Existed");
		}
	}
	
	public boolean isExisted()
	{
		if(flag==0) return true;
		else
			return false;
	}
	
	public void writeWB3(double[][][] kernel,double weight)
	{
		try {
			FileOutputStream w1=new FileOutputStream(f);	
			int i,j,k;
			for(i=0;i<kernel.length;i++)
			{
				for(j=0;j<kernel[i].length;j++)
				{
					for(k=0;k<kernel[i][j].length;k++)
					{
						w1.write(Double.toString(kernel[i][j][k]).getBytes());
						w1.write(" ".getBytes());
					}
				}
			}
			w1.write(" ".getBytes());
			w1.write(Double.toString(weight).getBytes());
			w1.write(" ".getBytes());
			w1.close();
		} catch (IOException e) {
			System.out.println("Write fail");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param targetKernel
	 * @return bias
	 */
	public double readWB3(double[][][] targetKernel)
	{
		try {
			FileInputStream i1=new FileInputStream(f);
			Scanner scanner=new Scanner(i1);
			double d;
			int i,j,k;
			for(i=0;i<targetKernel.length;i++)
			{
				for(j=0;j<targetKernel[i].length;j++)
				{
					for(k=0;k<targetKernel[i][j].length;k++)
					{
						targetKernel[i][j][k]=scanner.nextDouble();
					}
				}
			}
			d=scanner.nextDouble();
			scanner.close();
			i1.close();
			return d;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public void writeWB2(double[][] kernel,double weight)
	{
		try {
			FileOutputStream w1=new FileOutputStream(f);	
			int i,j;
			for(i=0;i<kernel.length;i++)
			{
				for(j=0;j<kernel[i].length;j++)
				{
					w1.write(Double.toString(kernel[i][j]).getBytes());
					w1.write(" ".getBytes());
				}
			}
			w1.write(" ".getBytes());
			w1.write(Double.toString(weight).getBytes());
			w1.write(" ".getBytes());
			w1.close();
		} catch (IOException e) {
			System.out.println("Write fail");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param targetKernel
	 * @return bias
	 */
	public double readWB2(double[][] targetKernel)
	{
		try {
			FileInputStream i1=new FileInputStream(f);
			Scanner scanner=new Scanner(i1);
			double d;
			int i,j;
			for(i=0;i<targetKernel.length;i++)
			{
				for(j=0;j<targetKernel[i].length;j++)
				{
						targetKernel[i][j]=scanner.nextDouble();
				}
			}
			d=scanner.nextDouble();
			scanner.close();
			i1.close();
			return d;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	/*public static void main(String[] args)
	{
		SaveWB s=new SaveWB("F:/ML/floor10.wb");
		double[][] d=new double[5][5];
		//s.writeWB2(d, 0);
		if(s.isExisted())
			s.readWB2(d);
	}*/
}

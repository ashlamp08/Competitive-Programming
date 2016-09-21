package com.spoj.practice.classic;

import java.io.*;
import java.util.*;

/**
 * Very good BIT question. Another solution exists which uses Mo's Algorithm, but is much slower than the BIT one.
 */
class DQuery
{
	public static void main(String[] args)
	{
		InputReader in = new InputReader(System.in);
		OutputWriter out = new OutputWriter(System.out);

//		Solver solver = new Solver(in, out);
//		solver.solve();

		BITSolver bitSolver = new BITSolver(in, out);
		bitSolver.solve();

		out.flush();

		in.close();
		out.close();
	}

	/**
	 * Solution using BIT. Much faster than Mo's Algorithm.
	 */
	static class BITSolver
	{
		int n, q, arr[], bit[];
		Query[] queries;
		InputReader in;
		OutputWriter out;

		void solve()
		{
			n = in.nextInt();
			arr = new int[n + 1];
			bit = new int[n + 1];

			for (int i = 1; i <= n; i++)
				arr[i] = in.nextInt();

			q = in.nextInt();
			queries = new Query[q];

			for (int i = 0; i < q; i++)
				queries[i] = new Query(i, in.nextInt(), in.nextInt());

			Arrays.sort(queries, new Comparator<Query>()
			{
				@Override public int compare(Query o1, Query o2)
				{
					return Integer.compare(o1.right, o2.right);
				}
			});

			int[] pos = new int[(int) 1e6 + 5];
			int[] ans = new int[q];
			int counter = 0;

			Arrays.fill(pos, -1);

			for (int i = 1; i <= n; i++)
			{
				// this element has already occurced in the array
				if (pos[arr[i]] != -1)
					update(pos[arr[i]], -1);

				pos[arr[i]] = i;
				update(i, 1);

				while (counter < q && i == queries[counter].right)
				{
					ans[queries[counter].index] = query(i) - query(queries[counter].left - 1);
					counter++;
				}
			}

			for (int i = 0; i < q; i++)
				out.println(ans[i]);
		}

		void update(int index, int value)
		{
			while (index <= n)
			{
				bit[index] += value;
				index += index & -index;
			}
		}

		int query(int index)
		{
			int answer = 0;

			while (index > 0)
			{
				answer += bit[index];
				index -= index & -index;
			}

			return answer;
		}

		class Query
		{
			int index, left, right;

			public Query(int index, int left, int right)
			{
				this.index = index;
				this.left = left;
				this.right = right;
			}

		}

		public BITSolver(InputReader in, OutputWriter out)
		{
			this.in = in;
			this.out = out;
		}

	}

	/**
	 * Solution using Mo's Algorithm.
	 */
	static class Solver
	{
		int n, q, sqrtN, size;
		int[] arr, count;
		Query[] queries;
		InputReader in;
		OutputWriter out;

		public Solver(InputReader in, OutputWriter out)
		{
			this.in = in;
			this.out = out;
		}

		void solve()
		{
			n = in.nextInt();
			arr = new int[n + 1];
			count = new int[(int) (1e6 + 5)];

			for (int i = 1; i <= n; i++)
			{
				arr[i] = in.nextInt();

				count[arr[i]]++;

				if (count[arr[i]] == 1)
					size++;
			}

			q = in.nextInt();
			sqrtN = (int) Math.sqrt(n);
			queries = new Query[q];

			for (int i = 0; i < q; i++)
			{
				int left, right;

				left = in.nextInt();
				right = in.nextInt();
				queries[i] = new Query(i, left, right);
			}

			Arrays.sort(queries, new Comparator<Query>()
			{
				@Override public int compare(Query o1, Query o2)
				{
					if (o1.blockNumber == o2.blockNumber)
						return Integer.compare(o1.right, o2.right);

					return Integer.compare(o1.blockNumber, o2.blockNumber);
				}
			});

			int left, right;
			int[] answer;

			left = 1;
			right = n;
			answer = new int[q];

			for (int i = 0; i < q; i++)
			{
				while (left < queries[i].left)
				{
					delete(arr[left]);
					left++;
				}

				while (left > queries[i].left)
				{
					left--;
					add(arr[left]);
				}

				while (right < queries[i].right)
				{
					right++;
					add(arr[right]);
				}

				while (right > queries[i].right)
				{
					delete(arr[right]);
					right--;
				}

				answer[queries[i].queryNumber] = size;
			}

			for (int i = 0; i < q; i++)
				out.println(answer[i]);

		}

		void add(int num)
		{
			count[num]++;

			if (count[num] == 1)
				size++;
		}

		void delete(int num)
		{
			count[num]--;

			if (count[num] == 0)
				size--;
		}

		class Query
		{
			int queryNumber, left, right, blockNumber;

			public Query(int queryNumber, int left, int right)
			{
				this.queryNumber = queryNumber;
				this.left = left;
				this.right = right;
				blockNumber = left / sqrtN;
			}

		}

	}

	static class InputReader
	{
		private InputStream stream;
		private byte[] buf = new byte[1024];
		private int curChar;
		private int numChars;

		public InputReader(InputStream stream)
		{
			this.stream = stream;
		}

		public int read()
		{
			if (numChars == -1)
				throw new InputMismatchException();

			if (curChar >= numChars)
			{
				curChar = 0;
				try
				{
					numChars = stream.read(buf);
				}
				catch (IOException e)
				{
					throw new InputMismatchException();
				}
				if (numChars <= 0)
					return -1;
			}

			return buf[curChar++];
		}

		public int nextInt()
		{
			int c = read();

			while (isSpaceChar(c))
				c = read();

			int sgn = 1;

			if (c == '-')
			{
				sgn = -1;
				c = read();
			}

			int res = 0;

			do
			{
				if (c < '0' || c > '9')
					throw new InputMismatchException();

				res *= 10;
				res += c & 15;

				c = read();
			} while (!isSpaceChar(c));

			return res * sgn;
		}

		public int[] nextIntArray(int arraySize)
		{
			int array[] = new int[arraySize];

			for (int i = 0; i < arraySize; i++)
				array[i] = nextInt();

			return array;
		}

		public long nextLong()
		{
			int c = read();

			while (isSpaceChar(c))
				c = read();

			int sign = 1;

			if (c == '-')
			{
				sign = -1;

				c = read();
			}

			long result = 0;

			do
			{
				if (c < '0' || c > '9')
					throw new InputMismatchException();

				result *= 10;
				result += c & 15;

				c = read();
			} while (!isSpaceChar(c));

			return result * sign;
		}

		public long[] nextLongArray(int arraySize)
		{
			long array[] = new long[arraySize];

			for (int i = 0; i < arraySize; i++)
				array[i] = nextLong();

			return array;
		}

		public float nextFloat() // problematic
		{
			float result, div;
			byte c;

			result = 0;
			div = 1;
			c = (byte) read();

			while (c <= ' ')
				c = (byte) read();

			boolean isNegative = (c == '-');

			if (isNegative)
				c = (byte) read();

			do
			{
				result = result * 10 + c - '0';
			} while ((c = (byte) read()) >= '0' && c <= '9');

			if (c == '.')
				while ((c = (byte) read()) >= '0' && c <= '9')
					result += (c - '0') / (div *= 10);

			if (isNegative)
				return -result;

			return result;
		}

		public double nextDouble() // not completely accurate
		{
			double ret = 0, div = 1;
			byte c = (byte) read();

			while (c <= ' ')
				c = (byte) read();

			boolean neg = (c == '-');

			if (neg)
				c = (byte) read();

			do
			{
				ret = ret * 10 + c - '0';
			} while ((c = (byte) read()) >= '0' && c <= '9');

			if (c == '.')
				while ((c = (byte) read()) >= '0' && c <= '9')
					ret += (c - '0') / (div *= 10);

			if (neg)
				return -ret;

			return ret;
		}

		public String next()
		{
			int c = read();

			while (isSpaceChar(c))
				c = read();

			StringBuilder res = new StringBuilder();

			do
			{
				res.appendCodePoint(c);

				c = read();
			} while (!isSpaceChar(c));

			return res.toString();
		}

		public boolean isSpaceChar(int c)
		{
			return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
		}

		public String nextLine()
		{
			int c = read();

			StringBuilder result = new StringBuilder();

			do
			{
				result.appendCodePoint(c);

				c = read();
			} while (!isNewLine(c));

			return result.toString();
		}

		public boolean isNewLine(int c)
		{
			return c == '\n';
		}

		public void close()
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	static class OutputWriter
	{
		private PrintWriter writer;

		public OutputWriter(OutputStream stream)
		{
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					stream)));
		}

		public OutputWriter(Writer writer)
		{
			this.writer = new PrintWriter(writer);
		}

		public void println(int x)
		{
			writer.println(x);
		}

		public void print(int x)
		{
			writer.print(x);
		}

		public void println(int array[], int size)
		{
			for (int i = 0; i < size; i++)
				println(array[i]);
		}

		public void print(int array[], int size)
		{
			for (int i = 0; i < size; i++)
				print(array[i] + " ");
		}

		public void println(long x)
		{
			writer.println(x);
		}

		public void print(long x)
		{
			writer.print(x);
		}

		public void println(long array[], int size)
		{
			for (int i = 0; i < size; i++)
				println(array[i]);
		}

		public void print(long array[], int size)
		{
			for (int i = 0; i < size; i++)
				print(array[i]);
		}

		public void println(float num)
		{
			writer.println(num);
		}

		public void print(float num)
		{
			writer.print(num);
		}

		public void println(double num)
		{
			writer.println(num);
		}

		public void print(double num)
		{
			writer.print(num);
		}

		public void println(String s)
		{
			writer.println(s);
		}

		public void print(String s)
		{
			writer.print(s);
		}

		public void println()
		{
			writer.println();
		}

		public void printSpace()
		{
			writer.print(" ");
		}

		public void flush()
		{
			writer.flush();
		}

		public void close()
		{
			writer.close();
		}

	}

}

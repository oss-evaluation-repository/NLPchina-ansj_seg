package org.ansj.ansj_lucene_plug;

import org.ansj.library.DicLibrary;
import org.ansj.lucene9.AnsjAnalyzer;
import org.ansj.lucene9.AnsjAnalyzer.TYPE;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.Test;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

public class IndexTest {

	@Test
	public void testQuery() throws IOException {
		Analyzer ca = new AnsjAnalyzer(TYPE.query_ansj);
		String content = ("\n\n\n\n\n\n\n我从小就不由自主地认为自己长大以后一定得成为一个象我父亲一样的画家, 可能是父母潜移默化的影响。其实我根本不知道作为画家意味着什么，我是否喜欢，最重要的是否适合我，我是否有这个才华。其实人到中年的我还是不确定我最喜欢什么，最想做的是什么？我相信很多人和我一样有同样的烦恼。毕竟不是每个人都能成为作文里的宇航员，科学家和大教授。知道自己适合做什么，喜欢做什么，能做好什么其实是个非常困难的问题。"
				+ "幸运的是，我想我的孩子不会为这个太过烦恼。通过老大，我慢慢发现美国高中的一个重要功能就是帮助学生分析他们的专长和兴趣，从而帮助他们选择大学的专业和未来的职业。我觉得帮助一个未成形的孩子找到她未来成长的方向是个非常重要的过程。"
				+ "美国高中都有专门的职业顾问，通过接触不同的课程，和各种心理，个性，兴趣很多方面的问答来帮助每个学生找到最感兴趣的专业。这样的教育一般是要到高年级才开始， 可老大因为今年上计算机的课程就是研究一个职业走向的软件项目，所以她提前做了这些考试和面试。看来以后这样的教育会慢慢由电脑来测试了。老大带回家了一些试卷，我挑出一些给大家看看。这门课她花了2个多月才做完，这里只是很小的一部分。"
				+ "在测试里有这样的一些问题："
				+ "你是个喜欢动手的人吗？ 你喜欢修东西吗？你喜欢体育运动吗？你喜欢在室外工作吗？你是个喜欢思考的人吗？你喜欢数学和科学课吗？你喜欢一个人工作吗？你对自己的智力自信吗？你的创造能力很强吗？你喜欢艺术，音乐和戏剧吗？  你喜欢自由自在的工作环境吗？你喜欢尝试新的东西吗？ 你喜欢帮助别人吗？你喜欢教别人吗？你喜欢和机器和工具打交道吗？你喜欢当领导吗？你喜欢组织活动吗？你什么和数字打交道吗？");
		TokenStream ts = ca.tokenStream(content, new StringReader(content));

		System.out.println("start: " + (new Date()));
		long before = System.currentTimeMillis();
		ts.reset();
		while (ts.incrementToken()) {
			System.out.println(ts.getAttribute(CharTermAttribute.class));
		}
		ts.close();
		long now = System.currentTimeMillis();
		System.out.println("time: " + (now - before) / 1000.0 + " s");
	}

	@Test
	public void testDic() throws IOException {
		DicLibrary.put(DicLibrary.DEFAULT, "../../library/default.dic");
		Analyzer ca = new AnsjAnalyzer(TYPE.dic_ansj);
		String content = ("\n\n\n\n\n\n\n我从小就不由自主地认为自己长大以后一定得成为一个象我父亲一样的画家, 可能是父母潜移默化的影响。其实我根本不知道作为画家意味着什么，我是否喜欢，最重要的是否适合我，我是否有这个才华。其实人到中年的我还是不确定我最喜欢什么，最想做的是什么？我相信很多人和我一样有同样的烦恼。毕竟不是每个人都能成为作文里的宇航员，科学家和大教授。知道自己适合做什么，喜欢做什么，能做好什么其实是个非常困难的问题。"
				+ "幸运的是，我想我的孩子不会为这个太过烦恼。通过老大，我慢慢发现美国高中的一个重要功能就是帮助学生分析他们的专长和兴趣，从而帮助他们选择大学的专业和未来的职业。我觉得帮助一个未成形的孩子找到她未来成长的方向是个非常重要的过程。"
				+ "美国高中都有专门的职业顾问，通过接触不同的课程，和各种心理，个性，兴趣很多方面的问答来帮助每个学生找到最感兴趣的专业。这样的教育一般是要到高年级才开始， 可老大因为今年上计算机的课程就是研究一个职业走向的软件项目，所以她提前做了这些考试和面试。看来以后这样的教育会慢慢由电脑来测试了。老大带回家了一些试卷，我挑出一些给大家看看。这门课她花了2个多月才做完，这里只是很小的一部分。"
				+ "在测试里有这样的一些问题："
				+ "你是个喜欢动手的人吗？ 你喜欢修东西吗？你喜欢体育运动吗？你喜欢在室外工作吗？你是个喜欢思考的人吗？你喜欢数学和科学课吗？你喜欢一个人工作吗？你对自己的智力自信吗？你的创造能力很强吗？你喜欢艺术，音乐和戏剧吗？  你喜欢自由自在的工作环境吗？你喜欢尝试新的东西吗？ 你喜欢帮助别人吗？你喜欢教别人吗？你喜欢和机器和工具打交道吗？你喜欢当领导吗？你喜欢组织活动吗？你什么和数字打交道吗？");
		TokenStream ts = ca.tokenStream(content, new StringReader(content));

		System.out.println("start: " + (new Date()));
		long before = System.currentTimeMillis();
		ts.reset();
		while (ts.incrementToken()) {
			System.out.println(ts.getAttribute(CharTermAttribute.class));
		}
		ts.close();
		long now = System.currentTimeMillis();
		System.out.println("time: " + (now - before) / 1000.0 + " s");
	}

	@Test
	public void testIndex() throws IOException {
		Analyzer ca = new AnsjAnalyzer(TYPE.index_ansj);
		String content = ("\n\n\n\n\n\n\n我从小就不由自主地认为自己长大以后一定得成为一个象我父亲一样的画家, 可能是父母潜移默化的影响。其实我根本不知道作为画家意味着什么，我是否喜欢，最重要的是否适合我，我是否有这个才华。其实人到中年的我还是不确定我最喜欢什么，最想做的是什么？我相信很多人和我一样有同样的烦恼。毕竟不是每个人都能成为作文里的宇航员，科学家和大教授。知道自己适合做什么，喜欢做什么，能做好什么其实是个非常困难的问题。"
				+ "幸运的是，我想我的孩子不会为这个太过烦恼。通过老大，我慢慢发现美国高中的一个重要功能就是帮助学生分析他们的专长和兴趣，从而帮助他们选择大学的专业和未来的职业。我觉得帮助一个未成形的孩子找到她未来成长的方向是个非常重要的过程。"
				+ "美国高中都有专门的职业顾问，通过接触不同的课程，和各种心理，个性，兴趣很多方面的问答来帮助每个学生找到最感兴趣的专业。这样的教育一般是要到高年级才开始， 可老大因为今年上计算机的课程就是研究一个职业走向的软件项目，所以她提前做了这些考试和面试。看来以后这样的教育会慢慢由电脑来测试了。老大带回家了一些试卷，我挑出一些给大家看看。这门课她花了2个多月才做完，这里只是很小的一部分。"
				+ "在测试里有这样的一些问题："
				+ "你是个喜欢动手的人吗？ 你喜欢修东西吗？你喜欢体育运动吗？你喜欢在室外工作吗？你是个喜欢思考的人吗？你喜欢数学和科学课吗？你喜欢一个人工作吗？你对自己的智力自信吗？你的创造能力很强吗？你喜欢艺术，音乐和戏剧吗？  你喜欢自由自在的工作环境吗？你喜欢尝试新的东西吗？ 你喜欢帮助别人吗？你喜欢教别人吗？你喜欢和机器和工具打交道吗？你喜欢当领导吗？你喜欢组织活动吗？你什么和数字打交道吗？");
		TokenStream ts = ca.tokenStream(content, new StringReader(content));

		System.out.println("start: " + (new Date()));
		long before = System.currentTimeMillis();
		ts.reset();
		while (ts.incrementToken()) {
			System.out.println(ts.getAttribute(CharTermAttribute.class));
		}
		ts.close();
		long now = System.currentTimeMillis();
		System.out.println("time: " + (now - before) / 1000.0 + " s");
	}

	@Test
	public void indexTest() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException {
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new AnsjAnalyzer(TYPE.index_ansj));
		Directory directory = null;
		IndexWriter iwriter = null;

		IndexWriterConfig ic = new IndexWriterConfig(analyzer);

		// 建立内存索引对象
		directory = new ByteBuffersDirectory();
		iwriter = new IndexWriter(directory, ic);
		addContent(iwriter, "助推企业转型升级提供强有力的技术支持和服保障。中心的建成将使青岛的服务器承载能力突破10万台，达到世界一流水平。");
		addContent(iwriter, "涉及民生的部分商品和服务成本监审政策");
		addContent(iwriter, "我穿着和服");
		iwriter.commit();
		iwriter.close();

		System.out.println("索引建立完毕");

		Analyzer queryAnalyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.dic_ansj);

		System.out.println("index ok to search!");
		search(queryAnalyzer, directory, "\"和服\"");

	}

	private void search(Analyzer queryAnalyzer, Directory directory, String queryStr) throws CorruptIndexException, IOException, ParseException {
		IndexSearcher isearcher;
		DirectoryReader directoryReader = DirectoryReader.open(directory);
		// 查询索引
		isearcher = new IndexSearcher(directoryReader);
		QueryParser tq = new QueryParser("text", queryAnalyzer);
		Query query = tq.parse(queryStr);
		System.out.println(query);
		TopDocs hits = isearcher.search(query, 5);
		System.out.println(queryStr + ":共找到" + hits.totalHits + "条记录!");
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			int docId = hits.scoreDocs[i].doc;
			Document document = isearcher.storedFields().document(docId);
			System.out.println(toHighlighter(queryAnalyzer, query, document));
		}
	}

	/**
	 * 高亮设置
	 * 
	 * @param query
	 * @param doc
	 * @param field
	 * @return
	 */
	private String toHighlighter(Analyzer analyzer, Query query, Document doc) {
		String field = "text";
		try {
			SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
			Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(query));
			TokenStream tokenStream1 = analyzer.tokenStream("text", new StringReader(doc.get(field)));
			String highlighterStr = highlighter.getBestFragment(tokenStream1, doc.get(field));
			return highlighterStr == null ? doc.get(field) : highlighterStr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void addContent(IndexWriter iwriter, String text) throws CorruptIndexException, IOException {
		Document doc = new Document();
		IndexableField field = new TextField("text", text, Store.YES);
		doc.add(field);
		iwriter.addDocument(doc);
	}

	@Test
	public void poreterTest() {
		PorterStemmer ps = new PorterStemmer();
		System.out.println(ps.stem());
	}

}

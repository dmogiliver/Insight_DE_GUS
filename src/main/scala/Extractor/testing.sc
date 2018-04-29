import Extractor.GithubCommitExtractor.{extractLanguage, extractPackages}
import play.api.libs.json.{JsValue, Json}



def parseMetaData(rawJson: String): List[List[String]] = {
  /**
    *
    * @param input
    * @return
    */
  def removeObjectId(input: String): String = input.replaceFirst("""ObjectId\(\s(\"[0-9a-z]*\")\s\)""", "$1")
  
  def filesInfo(filesObject: JsValue): List[List[String]] = {
    val fileFields = List("filename", "status", "patch") //commit/files/#/
    val files = (filesObject \ "files").get.as[List[Map[String, JsValue]]]
    
    def getIfDefined(file: Map[String, JsValue])(key: String): String =
      if (file.isDefinedAt(key)) file(key).as[String] else ""
    
    files.map(x => fileFields.map(getIfDefined(x)(_)))
  }
  
  def commitInfo(commit: JsValue): List[String] = {
    val notFound = (Json.parse("""{"NotFound": ""}""") \ "NotFound").get // Way to avoid squawking if the value isn't where it's supposed to be. There has to be a better way.
    List(
      commit \ "commit" \ "committer" \ "date",
      commit \ "commit" \ "committer" \ "email",
      commit \ "commit" \ "message"
    ).map(_.getOrElse(notFound).as[String])
  }
  
  // Actually do the work
  val commit = Json.parse(removeObjectId(rawJson))
  
  filesInfo(commit).map(commitInfo(commit) ::: _)
}

def extract(rawJson: String) = {
  for (file <- parseMetaData(rawJson) ) yield file match {
    case date :: email :: message :: filename :: status :: patch :: Nil =>
      (date, extractLanguage(filename), extractPackages(extractLanguage(filename), patch.stripMargin))
  }
//  for (file <- parseMetaData(rawJson) )
//    yield file match {
//      case date :: email :: message :: filename :: status :: patch :: Nil =>
//        extractPackages(extractLanguage(filename), patch).map{
//          case (count, packageName) => (date, email, message, filename, extractLanguage(filename), packageName, count)
//        }
//      case _ => {
//        println("fuck")
//        throw new Error("Incorrect output from something")
//      }
//    }
}

val testJson = """{ "_id" : "5adfe2126480fd219823198f", "sha" : "5741974d603e45fa1098d245b6aab71c83b890da", "commit" : { "author" : { "name" : "Sun, Yizhou", "email" : "yizhous@telenav.com", "date" : "2018-04-25T02:03:48Z" }, "committer" : { "name" : "Sun, Yizhou", "email" : "yizhous@telenav.com", "date" : "2018-04-25T02:03:48Z" }, "message" : "Complete rangeBitwise", "tree" : { "sha" : "7a04dde05eb416ac8f82f806a33dbdae6eb7a539", "url" : "https://api.github.com/repos/Yizhou-Sun/myCode/git/trees/7a04dde05eb416ac8f82f806a33dbdae6eb7a539" }, "url" : "https://api.github.com/repos/Yizhou-Sun/myCode/git/commits/5741974d603e45fa1098d245b6aab71c83b890da", "comment_count" : 0, "verification" : { "verified" : false, "reason" : "unsigned", "signature" : null, "payload" : null } }, "url" : "https://api.github.com/repos/Yizhou-Sun/myCode/commits/5741974d603e45fa1098d245b6aab71c83b890da", "html_url" : "https://github.com/Yizhou-Sun/myCode/commit/5741974d603e45fa1098d245b6aab71c83b890da", "comments_url" : "https://api.github.com/repos/Yizhou-Sun/myCode/commits/5741974d603e45fa1098d245b6aab71c83b890da/comments", "author" : null, "committer" : null, "parents" : [ { "sha" : "17d8dda0154100e125dfdb0fd0dd0bac59262af3", "url" : "https://api.github.com/repos/Yizhou-Sun/myCode/commits/17d8dda0154100e125dfdb0fd0dd0bac59262af3", "html_url" : "https://github.com/Yizhou-Sun/myCode/commit/17d8dda0154100e125dfdb0fd0dd0bac59262af3" } ], "stats" : { "total" : 130, "additions" : 128, "deletions" : 2 }, "files" : [ { "sha" : "3f27209b248f47a54f20bd756a543fe0c8283353", "filename" : "MainClass.java", "status" : "modified", "additions" : 2, "deletions" : 2, "changes" : 4, "blob_url" : "https://github.com/Yizhou-Sun/myCode/blob/5741974d603e45fa1098d245b6aab71c83b890da/MainClass.java", "raw_url" : "https://github.com/Yizhou-Sun/myCode/raw/5741974d603e45fa1098d245b6aab71c83b890da/MainClass.java", "contents_url" : "https://api.github.com/repos/Yizhou-Sun/myCode/contents/MainClass.java?ref=5741974d603e45fa1098d245b6aab71c83b890da", "patch" : "@@ -7,9 +7,9 @@ public static void main(String[] args) {\n         // int[] nums = {1,2,3,4,5,6,7};\n         // int num = 231;\n         // String s = \"AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT\";\n-        char[][] grid = {{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}};\n+        // char[][] grid = {{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}};\n         Solution solution = new Solution();\n-        int res = solution.numIslands(grid);\n+        int res = solution.rangeBitwiseAnd(5, 7);\n         System.out.println(res);\n \n         // for (int i : nums) {" }, { "sha" : "7d3cb2e77fa8b2a406ef62546419b0e852e16acb", "filename" : "Solution.java", "status" : "modified", "additions" : 96, "deletions" : 0, "changes" : 96, "blob_url" : "https://github.com/Yizhou-Sun/myCode/blob/5741974d603e45fa1098d245b6aab71c83b890da/Solution.java", "raw_url" : "https://github.com/Yizhou-Sun/myCode/raw/5741974d603e45fa1098d245b6aab71c83b890da/Solution.java", "contents_url" : "https://api.github.com/repos/Yizhou-Sun/myCode/contents/Solution.java?ref=5741974d603e45fa1098d245b6aab71c83b890da", "patch" : "@@ -2,6 +2,102 @@\n import java.lang.*;\n \n public class Solution {\n+    // 208\n+    // Implement a trie with insert, search, and startsWith methods.\n+    // Note:\n+    // You may assume that all inputs are consist of lowercase letters a-z.\n+    // in java file\n+\n+    // 207\n+    // There are a total of n courses you have to take, labeled from 0 to n - 1.\n+\n+    // Some courses may have prerequisites, for example to take course 0 you have to first take course 1, which is expressed as a pair: [0,1]\n+\n+    // Given the total number of courses and a list of prerequisite pairs, is it possible for you to finish all courses?\n+\n+    // For example:\n+\n+    // 2, [[1,0]]\n+    // There are a total of 2 courses to take. To take course 1 you should have finished course 0. So it is possible.\n+\n+    // 2, [[1,0],[0,1]]\n+    // There are a total of 2 courses to take. To take course 1 you should have finished course 0, and to take course 0 you should also have finished course 1. So it is impossible.\n+\n+    // Note:\n+    // The input prerequisites is a graph represented by a list of edges, not adjacency matrices. Read more about how a graph is represented.\n+    // You may assume that there are no duplicate edges in the input prerequisites.\n+    public boolean canFinish(int numCourses, int[][] prerequisites) {\n+        return false;\n+    }\n+    // 206\n+    // Reverse a singly linked list.\n+    public ListNode reverseList(ListNode head) {\n+        return null;\n+    }\n+    // 205\n+    // Given two strings s and t, determine if they are isomorphic.\n+\n+    // Two strings are isomorphic if the characters in s can be replaced to get t.\n+\n+    // All occurrences of a character must be replaced with another character while preserving the order of characters. No two characters may map to the same character but a character may map to itself.\n+\n+    // For example,\n+    // Given \"egg\", \"add\", return true.\n+\n+    // Given \"foo\", \"bar\", return false.\n+\n+    // Given \"paper\", \"title\", return true.\n+\n+    // Note:\n+    // You may assume both s and t have the same length.\n+    public boolean isIsomorphic(String s, String t) {\n+        return false;\n+    }\n+    // 204\n+    // Description:\n+\n+    // Count the number of prime numbers less than a non-negative number, n.\n+    public int countPrimes(int n) {\n+        return 0;\n+    }\n+    // 203\n+    // Remove all elements from a linked list of integers that have value val.\n+\n+    // Example\n+    // Given: 1 --> 2 --> 6 --> 3 --> 4 --> 5 --> 6, val = 6\n+    // Return: 1 --> 2 --> 3 --> 4 --> 5\n+    public ListNode removeElements(ListNode head, int val) {\n+        return null;\n+    }\n+\n+    // 202\n+    // Write an algorithm to determine if a number is \"happy\".\n+\n+    // A happy number is a number defined by the following process: Starting with any positive integer, replace the number by the sum of the squares of its digits, and repeat the process until the number equals 1 (where it will stay), or it loops endlessly in a cycle which does not include 1. Those numbers for which this process ends in 1 are happy numbers.\n+\n+    // Example: 19 is a happy number\n+\n+    // 12 + 92 = 82\n+    // 82 + 22 = 68\n+    // 62 + 82 = 100\n+    // 12 + 02 + 02 = 1\n+    public boolean isHappy(int n) {\n+        return false;\n+    }\n+\n+    // 201\n+    // Given a range [m, n] where 0 <= m <= n <= 2147483647, return the bitwise AND of all numbers in this range, inclusive.\n+    // For example, given the range [5, 7], you should return 4.\n+    public int rangeBitwiseAnd(int m, int n) {\n+        int i = 0;\n+        while (m != n) {\n+            m >>= 1;\n+            n >>= 1;\n+            i++;\n+        }\n+        return m << i;\n+    }\n+\n     // 200\n     // Given a 2d grid map of '1's (land) and '0's (water), count the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.\n     // Example 1:" }, { "sha" : "059b215b421fc0052495cf01e7131032675032be", "filename" : "Trie.java", "status" : "added", "additions" : 30, "deletions" : 0, "changes" : 30, "blob_url" : "https://github.com/Yizhou-Sun/myCode/blob/5741974d603e45fa1098d245b6aab71c83b890da/Trie.java", "raw_url" : "https://github.com/Yizhou-Sun/myCode/raw/5741974d603e45fa1098d245b6aab71c83b890da/Trie.java", "contents_url" : "https://api.github.com/repos/Yizhou-Sun/myCode/contents/Trie.java?ref=5741974d603e45fa1098d245b6aab71c83b890da", "patch" : "@@ -0,0 +1,30 @@\n+class Trie {\n+\n+    /** Initialize your data structure here. */\n+    public Trie() {\n+\n+    }\n+\n+    /** Inserts a word into the trie. */\n+    public void insert(String word) {\n+\n+    }\n+\n+    /** Returns if the word is in the trie. */\n+    public boolean search(String word) {\n+        return false;\n+    }\n+\n+    /** Returns if there is any word in the trie that starts with the given prefix. */\n+    public boolean startsWith(String prefix) {\n+        return false;\n+    }\n+}\n+\n+/**\n+ * Your Trie object will be instantiated and called as such:\n+ * Trie obj = new Trie();\n+ * obj.insert(word);\n+ * boolean param_2 = obj.search(word);\n+ * boolean param_3 = obj.startsWith(prefix);\n+ */\n\\ No newline at end of file" } ] }"""
//parseMetaData(testJson)
extract(testJson)

val testPatch = "@@ -8,11 +8,21 @@ object SparkOnFile {\n            import asd.asd.asd.asd   val sc = new SparkContext(sparkConf);\n \n     //val textFile = sc.textFile(\"hdfs://spark1:8020/world-count.txt\")\n-    val textFile = sc.textFile(\"C:\\\\Users\\\\haha174\\\\Desktop\\\\data\\\\world-count.txt\")\n-    val counts = textFile.flatMap(line => line.split(\"-\"))\n+    val textFile = sc.textFile(\"C:\\\\Users\\\\wchen129\\\\Desktop\\\\data\\\\sparkdata\\\\world-count.txt\")\n+    var str=\"haha wo\";\n+    var str1=str.split(\" \");\n+    var str2=str.split(\" \",3);\n+    var str3=str.split(\" \")(0);\n+\n+    str1.foreach(count=>println(\"str1+ \"+count))\n+    str2.foreach(count=>println(\"str2+ \"+count))\n+    str3.foreach(count=>println(\"str3+ \"+count))\n+    println(str3)\n+    val counts = textFile.flatMap(line => line.split(\" \")(0))\n       .map(word => (word, 1))\n       .reduceByKey(_ + _)\n     counts.foreach(count=>println(count._1+\" appeard \"+count._2+\" times\"))\n     //counts.saveAsTextFile(\"hdfs://spark1:8020/world-count-result.txt\")\n+\n   }\n }\n\\ No newline at end of file"
extractPackages("scala", testPatch)




java_typing_game
================

## プログラム
単語版 word.TypingWords.java  
文章版 document.TypingDocument.java

## 文章ソース
単語版は words/  
文章版は documents/  
にテキストファイルを保存。（ディレクトリは変更可能で，実行時に指定する。）

### 単語版
テキストベースのファイルに， 1 行 1 つずつ，  
余計な文字を入れず，単語と改行のみで書く。最後の行に改行を入れてもよい。

### 文章版
テキストベースのファイルに， 1 段落 1 行で文章を書く。  
先頭には自動的にタブが入力される。  
最後の段落となる文章には改行を挿入しない。  
また，ディレクトリには index.txt が存在する必要がある。  
index.txt の中味は次のように記述する。  
title1 file1-1 file1-2 ...  
title2 file2-1 file2-2 ...  
...  
titlen filen-1 filen-2 ...  
title は文章のタイトルで， file が文章のファイル名になる。  
ファイル数は 1 以上で，複数のファイルがある場合は，  
改ページによって連結される。

## コンパイル方法
Unix 系の場合， java\_typing\_game ディレクトリで  
sh compile.sh  
を実行

## 実行方法
Unix 系の場合， java\_typing\_game ディレクトリで，どちらかを実行  
java -cp bin word.TypingWords words/text\_path  
java -cp bin document.TypingDocument doc\_path  
ただし text\_path はファイル名， doc\_path は，ディレクトリ名  
java -cp bin document.TypingDocument documents  
java -cp bin document.TypingWords words/sample.txt  
とやると，サンプルのテキストで実行できる。  

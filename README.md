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
余計な文字を入れず単語を書く。

### 文章版
テキストベースのファイルに， 1 段落 1 行で文章を書く。  
先頭には自動的にタブが入力される。  
最後の行は改行を挿入しない。

## コンパイル方法
Unix 系の場合， java\_typing\_game ディレクトリで  
sh compile.sh  
を実行

## 実行方法
Unix 系の場合， java\_typing\_game ディレクトリで，どちらかを実行  
java -cp bin word.TypingWords words/text\_path  
java -cp bin document.TypingDocument documents/doc\_path  
ただし text\_path, doc\_path は，ファイル名

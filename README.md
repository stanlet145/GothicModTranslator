# GothicModTranslator

GothicModTranslator - designed to translate specific scenarios and dialogues

Files folder needs to be on the same level as application. You can copy directory tree from resources.

Takes file content from given directory (files/from) and writes changes to equal file in other directory (files/to).
Changes that will be applied:

1) Lines including double slashes will have content behind them replaced (files/from.givenFile.d - > files/to.givenFile.d) and saved as new file in files/direct
2) 
package parser.utils

import parser.exceptions.NextTokenNullException
import parser.exceptions.ParserException
import utils.ClassType
import utils.Token

class Utils {
    companion object {
        var readTokens = mutableListOf<Token>()
        var errorTokens = mutableListOf<Token>()

        fun MutableList<Token>.hasNextToken(): Boolean = this.isNotEmpty()

        fun MutableList<Token>.nextToken(): Token {
            if(this.hasNextToken()) {
                val token = this[0]
                this.removeAt(0)
                readTokens.add(token)
                return token
            }
            throw NextTokenNullException()
        }

        fun MutableList<Token>.putTokenBack(token: Token) {
            this.add(0, token)
        }

        fun MutableList<Token>.readTokens(): List<Token> = readTokens

        fun MutableList<Token>.lastValidToken(): Token? {
            return try {
                readTokens.last()
            } catch (e : NoSuchElementException) {
                null
            }
        }

        fun MutableList<Token>.removeLastReadTokenAndPutBackInTokenList() {
            val token = readTokens.removeAt(readTokens.lastIndex)
            this.add(0, token)
        }

        fun MutableList<Token>.peekNextToken(): Token {
            if(this.hasNextToken()) {
                return this[0]
            }
            throw NextTokenNullException()
        }

        fun MutableList<Token>.addParserException(e: ParserException) {
            val receivedTokenLine = e.receivedToken?.line ?: 0
            val receivedTokenValue = e.receivedToken?.value ?: ""
            val token = Token(ClassType.createUnknownErrorType(), receivedTokenValue, receivedTokenLine, e.expectedTokens)
            errorTokens.add(token)
        }

        fun MutableList<Token>.getErrors(): List<Token> = errorTokens
    }
}
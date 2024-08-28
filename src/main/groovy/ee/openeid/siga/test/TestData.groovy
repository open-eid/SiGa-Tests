package ee.openeid.siga.test

class TestData {

    // Default files
    static final String DEFAULT_HASHCODE_CONTAINER = "UEsDBAoAAAgAACNhek6KIflFHwAAAB8AAAAIAAAAbWltZXR5cGVhcHBsaWNhdGlvbi92bmQuZXRzaS5hc2ljLWUremlwUEsDBBQACAgIACNhek4AAAAAAAAAAAAAAAAVAAAATUVUQS1JTkYvbWFuaWZlc3QueG1snZFLDsIwDESvUnmL0tIdiki74wRwgCg1YClxosatgNMTFvzEBth5RmO/kbzuT8FXM46ZIhto6yVUyC4OxAcDu+1GraDv1sEy7TGLvg9VWeP8kAamkXW0mbJmGzBrcTom5CG6KSCLfs/rG+jl7J48qhIbz9XTm7xXycrRQANPO+BAVsk5oQGbkidnpXRvZh5qlEx1KeEULi6UoPkeIUXXcpIvSNEJisoyog2/Ito/Gc3HB7orUEsHCDafv120AAAAuwEAAFBLAwQUAAgICAAjYXpOAAAAAAAAAAAAAAAAHQAAAE1FVEEtSU5GL2hhc2hjb2Rlcy1zaGEyNTYueG1sfc/NjoIwGIXhW2m+rUELqCkJ1QwqLEYmEeoYZ+dPEaSWkRYHevXqSlezPXmTJ8eftheBbrxWRSUp2H0MiMtDdSzkicKahRaB6cTPdyp/jFxN/KwQ3OJS1x3KGiGs353OKWiudF+3GtAzpZDIz59q/7X57kjjOWq+HKbO2Q7WJmajKzvpcX7xMMs+oqSigFRh+AMnHgz+A+x3IQ6T0E2Dv962LElwjGaLyIi4brvDjFzN3tzOqyZluCtJ/BIwdh33iQxej+5QSwcImEfmysUAAAADAQAAUEsDBBQACAgIACNhek4AAAAAAAAAAAAAAAAdAAAATUVUQS1JTkYvaGFzaGNvZGVzLXNoYTUxMi54bWx90E1vgjAAxvGvQno12vKSDRLQ6ICaSBw4wMnNrIXWQXkpMPHTT0/ztOuT/+GXx15dq1IZaSd5LRygLhBQqPiqCReFA5LYn5tgtbTZWbL7SOXSznlJ51T03aTkQ1nOm3PPHNBT2S/6aw+UR+oAFqU364d3abYfQ9jq2/p8NMWA/Hzs8HfGBVu7O884GRdIkgHnxfs+PxjpiQyogeQCJ7xFbekhH8sq2Wh7vQ4GtnYcoEh+o3eoaQH4H0Z91jRrzWUaPNZvQlxxYIUZmbkRHz+TqjVJhFUxRSe9Ybss3JUV3OZZ5mJz2rxaLMaabzSpFbTH2csHll7sWWQWRPJQPGkQ0jX9AYJ/T/0CUEsHCCG3/O8JAQAAWwEAAFBLAwQKAAAIAAAjYXpOGDZBl1c0AABXNAAAGAAAAE1FVEEtSU5GL3NpZ25hdHVyZXMwLnhtbDw/eG1sIHZlcnNpb249IjEuMCIgZW5jb2Rpbmc9IlVURi04IiBzdGFuZGFsb25lPSJubyI/Pjxhc2ljOlhBZEVTU2lnbmF0dXJlcyB4bWxuczphc2ljPSJodHRwOi8vdXJpLmV0c2kub3JnLzAyOTE4L3YxLjIuMSMiPjxkczpTaWduYXR1cmUgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiIElkPSJpZC1hOWZhZTAwNDk2YWUyMDNhNmE4YjkyYWRiZTc2MmJkMyI+PGRzOlNpZ25lZEluZm8+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48ZHM6U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxkc2lnLW1vcmUjZWNkc2Etc2hhMjU2Ii8+PGRzOlJlZmVyZW5jZSBJZD0ici1pZC0xIiBVUkk9InRlc3QudHh0Ij48ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjc2hhMjU2Ii8+PGRzOkRpZ2VzdFZhbHVlPlJuS1pvYk5XVnk4dTkyc0RMNFMyajFCVXpNVDVxVGd0NmhtOTBUZkFHUm89PC9kczpEaWdlc3RWYWx1ZT48L2RzOlJlZmVyZW5jZT48ZHM6UmVmZXJlbmNlIElkPSJyLWlkLTIiIFVSST0idGVzdDEudHh0Ij48ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjc2hhMjU2Ii8+PGRzOkRpZ2VzdFZhbHVlPk1GUkYzU0J3K1lrazhCZEdDRUd6bE1yeHljQzhxemJ6dmpRdVNUMHlrOE09PC9kczpEaWdlc3RWYWx1ZT48L2RzOlJlZmVyZW5jZT48ZHM6UmVmZXJlbmNlIFR5cGU9Imh0dHA6Ly91cmkuZXRzaS5vcmcvMDE5MDMjU2lnbmVkUHJvcGVydGllcyIgVVJJPSIjeGFkZXMtaWQtYTlmYWUwMDQ5NmFlMjAzYTZhOGI5MmFkYmU3NjJiZDMiPjxkczpUcmFuc2Zvcm1zPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48L2RzOlRyYW5zZm9ybXM+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPjxkczpEaWdlc3RWYWx1ZT50ZTRiSlh4Q0toRk1KQU5HRTRBVlc2d251bGxYSDBkbzVwMmwra2VQaldNPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpTaWduZWRJbmZvPjxkczpTaWduYXR1cmVWYWx1ZSBJZD0idmFsdWUtaWQtYTlmYWUwMDQ5NmFlMjAzYTZhOGI5MmFkYmU3NjJiZDMiPmNqcUlrUitNbTNna2FVZ0JtbExWbDNiTEk1ZWQ2WS9IZ2F2cDJLTFZvdkh4MFdlalhpOGdkL1p0SUFYMlJsd2Z2Q3VyVXRaaDJMd3pCRzFobWxrZk5DVlg2WThSQXlYVTFRZythcDFJSVI4Ti9LdzdvbTFDVk9Qbmwvc1AvdkZ0PC9kczpTaWduYXR1cmVWYWx1ZT48ZHM6S2V5SW5mbz48ZHM6WDUwOURhdGE+PGRzOlg1MDlDZXJ0aWZpY2F0ZT5NSUlENnpDQ0EwMmdBd0lCQWdJUVlTTXA3Q2hxMmJ0YnlHSTV1VzFucURBS0JnZ3Foa2pPUFFRREJEQmdNUXN3Q1FZRFZRUUdFd0pGUlRFYk1Ca0dBMVVFQ2d3U1Uwc2dTVVFnVTI5c2RYUnBiMjV6SUVGVE1SY3dGUVlEVlFSaERBNU9WRkpGUlMweE1EYzBOekF4TXpFYk1Ca0dBMVVFQXd3U1ZFVlRWQ0J2WmlCRlUxUkZTVVF5TURFNE1CNFhEVEU0TVRBeE9ERXdNelkwTVZvWERUSXpNVEF4TnpJeE5UazFPVm93ZnpFTE1Ba0dBMVVFQmhNQ1JVVXhLakFvQmdOVkJBTU1JVXJEbFVWUFVrY3NTa0ZCU3kxTFVrbFRWRXBCVGl3ek9EQXdNVEE0TlRjeE9ERVFNQTRHQTFVRUJBd0hTc09WUlU5U1J6RVdNQlFHQTFVRUtnd05Ta0ZCU3kxTFVrbFRWRXBCVGpFYU1CZ0dBMVVFQlJNUlVFNVBSVVV0TXpnd01ERXdPRFUzTVRnd2RqQVFCZ2NxaGtqT1BRSUJCZ1VyZ1FRQUlnTmlBQVRSWlRPcmcvSmVEOVdiRVRnU05KRUFBeUdDcFdMYkJWUExoQVhGTzRYOXh0SUZoblM1eklIZkpGNkRFQUN3Z0R5aHBwb1ZmN0U2S2NOeUVXRDRiUWYvLytIOVJjbzFHRFFoelYvaEYzODNSbUpFMnZGNTlEYzFMOGVGZ0ZKckJJK2pnZ0dyTUlJQnB6QUpCZ05WSFJNRUFqQUFNQTRHQTFVZER3RUIvd1FFQXdJR1FEQklCZ05WSFNBRVFUQS9NRElHQ3lzR0FRUUJnNUVoQVFJQk1DTXdJUVlJS3dZQkJRVUhBZ0VXRldoMGRIQnpPaTh2ZDNkM0xuTnJMbVZsTDBOUVV6QUpCZ2NFQUl2c1FBRUNNQjBHQTFVZERnUVdCQlNvdzdJc014QmJ0Z0ZJZzhCNmVuc0Q1b20raURDQmlnWUlLd1lCQlFVSEFRTUVmakI4TUFnR0JnUUFqa1lCQVRBSUJnWUVBSTVHQVFRd0V3WUdCQUNPUmdFR01Ba0dCd1FBamtZQkJnRXdVUVlHQkFDT1JnRUZNRWN3UlJZL2FIUjBjSE02THk5emF5NWxaUzlsYmk5eVpYQnZjMmwwYjNKNUwyTnZibVJwZEdsdmJuTXRabTl5TFhWelpTMXZaaTFqWlhKMGFXWnBZMkYwWlhNdkV3SkZUakFmQmdOVkhTTUVHREFXZ0JUQWhKa3B4RTZmT3dJMDlwbmhDbFlBQ0NrK2V6QnpCZ2dyQmdFRkJRY0JBUVJuTUdVd0xBWUlLd1lCQlFVSE1BR0dJR2gwZEhBNkx5OWhhV0V1WkdWdGJ5NXpheTVsWlM5bGMzUmxhV1F5TURFNE1EVUdDQ3NHQVFVRkJ6QUNoaWxvZEhSd09pOHZZeTV6YXk1bFpTOVVaWE4wWDI5bVgwVlRWRVZKUkRJd01UZ3VaR1Z5TG1OeWREQUtCZ2dxaGtqT1BRUURCQU9CaXdBd2dZY0NRUzBOb2h4ZC9ZRytINmNtYmw1dFJLQWZJTVk4UUk2WmtxVnFERjJiVUhabkxna3lTVW9lM014cU5VZmZvWmhkZDZhMU1ORGVFUGxOeDVES3NaNjJvNTk4QWtJQmZwT2lJbmllOENQTnRiekNFVDRsa3NhZm15dDFqcWRYS1NPQkd0aTU3Rkh6NnI1Y2dnTmV1enhScmtYVmtiL1lFYVVwTWtZdk5Db2tQUVZBdThWV0tTWT08L2RzOlg1MDlDZXJ0aWZpY2F0ZT48L2RzOlg1MDlEYXRhPjwvZHM6S2V5SW5mbz48ZHM6T2JqZWN0Pjx4YWRlczpRdWFsaWZ5aW5nUHJvcGVydGllcyB4bWxuczp4YWRlcz0iaHR0cDovL3VyaS5ldHNpLm9yZy8wMTkwMy92MS4zLjIjIiBUYXJnZXQ9IiNpZC1hOWZhZTAwNDk2YWUyMDNhNmE4YjkyYWRiZTc2MmJkMyI+PHhhZGVzOlNpZ25lZFByb3BlcnRpZXMgSWQ9InhhZGVzLWlkLWE5ZmFlMDA0OTZhZTIwM2E2YThiOTJhZGJlNzYyYmQzIj48eGFkZXM6U2lnbmVkU2lnbmF0dXJlUHJvcGVydGllcz48eGFkZXM6U2lnbmluZ1RpbWU+MjAxOS0wMi0yMlQxMTowNDoyNFo8L3hhZGVzOlNpZ25pbmdUaW1lPjx4YWRlczpTaWduaW5nQ2VydGlmaWNhdGU+PHhhZGVzOkNlcnQ+PHhhZGVzOkNlcnREaWdlc3Q+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPjxkczpEaWdlc3RWYWx1ZT56bXZtckd6SEY0aHc4eldJaXJwYjlTYm1oT3RSaW1PdUNNck9SRkZGNWlrPTwvZHM6RGlnZXN0VmFsdWU+PC94YWRlczpDZXJ0RGlnZXN0Pjx4YWRlczpJc3N1ZXJTZXJpYWw+PGRzOlg1MDlJc3N1ZXJOYW1lPkNOPVRFU1Qgb2YgRVNURUlEMjAxOCwyLjUuNC45Nz0jMGMwZTRlNTQ1MjQ1NDUyZDMxMzAzNzM0MzczMDMxMzMsTz1TSyBJRCBTb2x1dGlvbnMgQVMsQz1FRTwvZHM6WDUwOUlzc3Vlck5hbWU+PGRzOlg1MDlTZXJpYWxOdW1iZXI+MTI5MTE3Njk2MjcwMzM0MjM2NDYyOTUzNDI4MTIzNDU1OTQwNTIwPC9kczpYNTA5U2VyaWFsTnVtYmVyPjwveGFkZXM6SXNzdWVyU2VyaWFsPjwveGFkZXM6Q2VydD48L3hhZGVzOlNpZ25pbmdDZXJ0aWZpY2F0ZT48L3hhZGVzOlNpZ25lZFNpZ25hdHVyZVByb3BlcnRpZXM+PHhhZGVzOlNpZ25lZERhdGFPYmplY3RQcm9wZXJ0aWVzPjx4YWRlczpEYXRhT2JqZWN0Rm9ybWF0IE9iamVjdFJlZmVyZW5jZT0iI3ItaWQtMSI+PHhhZGVzOk1pbWVUeXBlPnRleHQvcGxhaW48L3hhZGVzOk1pbWVUeXBlPjwveGFkZXM6RGF0YU9iamVjdEZvcm1hdD48eGFkZXM6RGF0YU9iamVjdEZvcm1hdCBPYmplY3RSZWZlcmVuY2U9IiNyLWlkLTIiPjx4YWRlczpNaW1lVHlwZT50ZXh0L3BsYWluPC94YWRlczpNaW1lVHlwZT48L3hhZGVzOkRhdGFPYmplY3RGb3JtYXQ+PC94YWRlczpTaWduZWREYXRhT2JqZWN0UHJvcGVydGllcz48L3hhZGVzOlNpZ25lZFByb3BlcnRpZXM+PHhhZGVzOlVuc2lnbmVkUHJvcGVydGllcz48eGFkZXM6VW5zaWduZWRTaWduYXR1cmVQcm9wZXJ0aWVzPjx4YWRlczpTaWduYXR1cmVUaW1lU3RhbXAgSWQ9IlRTLWY4OGViZDdmLWFmZTAtNGJkZi1iZTI4LTRhMGE2YTNmMjllNCI+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48eGFkZXM6RW5jYXBzdWxhdGVkVGltZVN0YW1wIElkPSJFVFMtZjg4ZWJkN2YtYWZlMC00YmRmLWJlMjgtNGEwYTZhM2YyOWU0Ij5NSUFHQ1NxR1NJYjNEUUVIQXFDQU1JSUh1Z0lCQXpFUE1BMEdDV0NHU0FGbEF3UUNBd1VBTUd3R0N5cUdTSWIzRFFFSkVBRUVvRjBFV3pCWkFnRUJCZ1lFQUk5bkFRRXdNVEFOQmdsZ2hrZ0JaUU1FQWdFRkFBUWd0TkNWL2RDUmt3Rk1SZm5MTi9DbHQxMHBpMDJFOVRUSEtYbWl6anJPVFNNQ0NCZnpYUWxlL1R6NkdBOHlNREU1TURJeU1qRXhNRFF5TlZxZ2dnUVpNSUlFRlRDQ0F2MmdBd0lCQWdJUVRxejdiQ1A4VzQ1VUJaYTd0enRUVERBTkJna3Foa2lHOXcwQkFRc0ZBREI5TVFzd0NRWURWUVFHRXdKRlJURWlNQ0FHQTFVRUNnd1pRVk1nVTJWeWRHbG1hWFJ6WldWeWFXMXBjMnRsYzJ0MWN6RXdNQzRHQTFVRUF3d25WRVZUVkNCdlppQkZSU0JEWlhKMGFXWnBZMkYwYVc5dUlFTmxiblJ5WlNCU2IyOTBJRU5CTVJnd0ZnWUpLb1pJaHZjTkFRa0JGZ2x3YTJsQWMyc3VaV1V3SGhjTk1UUXdPVEF5TVRBd05qVXhXaGNOTWpRd09UQXlNVEF3TmpVeFdqQmRNUXN3Q1FZRFZRUUdFd0pGUlRFaU1DQUdBMVVFQ2d3WlFWTWdVMlZ5ZEdsbWFYUnpaV1Z5YVcxcGMydGxjMnQxY3pFTU1Bb0dBMVVFQ3d3RFZGTkJNUnd3R2dZRFZRUUREQk5FUlUxUElHOW1JRk5MSUZSVFFTQXlNREUwTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF5c2dyVm5WUHhIOGpOZ0NzSncweSs3Zm1tQkRUTS90TkIreGllbG5QOUtjdVErbnlUZ051MUpNcG5yeTdSaDRuZHI1NHJQTFhOR1ZkYi92c2dzaThCNTU4RGlzUFZVbjNSdXIzLzhYUStCQ2toVFFJZzFjU215Q3NXeEpnZWFRS0ppNldHVmFRV0IyaGUzNWFWaEw1RjZhZS9nelhUM3NHR3duV3VqWmtZOW81UmFwR1YxNSsvYjdVdis3aldZRkF4Y0Q2YmE1akkwMFJZL2dtc1d3S2IyMjZSbnovcFhLREJmdU4zb3g3eTUvbFpmNStNeUljVmUxcUplN1ZBSkdwSkZqTnErQkVFZHZmcXZKMVBpR1FFREpBUGhScWFoVmpCU3pxWmhKUW9MM0hJNDJOUkNGd2FydmRuWllvQ1B4amVZcEF5blRIZ05SN2tLR1gxaVE4T1FJREFRQUJvNEd3TUlHdE1BNEdBMVVkRHdFQi93UUVBd0lHd0RBV0JnTlZIU1VCQWY4RUREQUtCZ2dyQmdFRkJRY0RDREFkQmdOVkhRNEVGZ1FVSndTY1pReHpsenlTVnFaWHZpWHBLWkRWNU53d0h3WURWUjBqQkJnd0ZvQVV0VFFLbmFVdkVNWG5JUTYreExGbFJ4c0RkdjR3UXdZRFZSMGZCRHd3T2pBNG9EYWdOSVl5YUhSMGNITTZMeTkzZDNjdWMyc3VaV1V2Y21Wd2IzTnBkRzl5ZVM5amNteHpMM1JsYzNSZlpXVmpZM0pqWVM1amNtd3dEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSXEwMlNWS3dQMVVvbEtqcUFRZTdTVlkvS2dpKytHMmtxQWQ0MFVtTXFhOTRHVHU5MUxGWlI1VHZkb3laampuUTJpb1hoNUNWMmxmbFV5L2xVclpNRHBxRWU3SWJqWlc1K2I5bjVhQnZYWUpnRHVhOVNZak1PcmN5M3NpeXRxcThVYk5naDc5dWJZZ1doSGhKU25MV0s1WUorNXZRalRwT01kUnNMcC9EK0ZoVFVhNm1QMFVEWStVODIvdEZ1ZmtkOUhXNHpiYWxVV2hRZ25OWUkzb28wQ3NaMEhFeHV5bk9PWm1NMUJmOFB6RDZldGxMU0trWUIrbUI3N09tcWdmbHp6K0pqeWg0NW8rMzA1TVJ6SERGZUpaeDdXeEMrWFROV1EwWkZURmZjMG96eHh6VVdVbGZOZnBXeVFoMys0TGJlU1FSV3JOa2JOUmZDcFlvdHlNNkFZeGdnTVhNSUlERXdJQkFUQ0JrVEI5TVFzd0NRWURWUVFHRXdKRlJURWlNQ0FHQTFVRUNnd1pRVk1nVTJWeWRHbG1hWFJ6WldWeWFXMXBjMnRsYzJ0MWN6RXdNQzRHQTFVRUF3d25WRVZUVkNCdlppQkZSU0JEWlhKMGFXWnBZMkYwYVc5dUlFTmxiblJ5WlNCU2IyOTBJRU5CTVJnd0ZnWUpLb1pJaHZjTkFRa0JGZ2x3YTJsQWMyc3VaV1VDRUU2cysyd2ovRnVPVkFXV3U3YzdVMHd3RFFZSllJWklBV1VEQkFJREJRQ2dnZ0ZXTUJvR0NTcUdTSWIzRFFFSkF6RU5CZ3NxaGtpRzl3MEJDUkFCQkRBY0Jna3Foa2lHOXcwQkNRVXhEeGNOTVRrd01qSXlNVEV3TkRJMVdqQlBCZ2txaGtpRzl3MEJDUVF4UWdSQXdFaldKR1c3MnlzS3BnYVk1dmdqVmdtMWpVaUx5UXJuSnUwTittWGRNSW4xRnlUbktmNWx6L05zb1E0dHZ5L3J5QWNPL1B1NUh3QnozN0F3dDJBY2h6Q0J5QVlMS29aSWh2Y05BUWtRQWd3eGdiZ3dnYlV3Z2JJd2dhOEVGQUt4bCs5NHJ1Rng5cUZIWDFEcXpHVng4ZndMTUlHV01JR0JwSDh3ZlRFTE1Ba0dBMVVFQmhNQ1JVVXhJakFnQmdOVkJBb01HVUZUSUZObGNuUnBabWwwYzJWbGNtbHRhWE5yWlhOcmRYTXhNREF1QmdOVkJBTU1KMVJGVTFRZ2IyWWdSVVVnUTJWeWRHbG1hV05oZEdsdmJpQkRaVzUwY21VZ1VtOXZkQ0JEUVRFWU1CWUdDU3FHU0liM0RRRUpBUllKY0d0cFFITnJMbVZsQWhCT3JQdHNJL3hiamxRRmxydTNPMU5NTUEwR0NTcUdTSWIzRFFFQkFRVUFCSUlCQUI1ZVhMTFZWNFREcVFMTUxaelFOL2QwcnQxdDQxVWp5Mk9wa0k0TGhFUmVTU0dtZW5NbGRhM0N5SDZFeU84TFBWV3ZuWmFXcFdZUmxPclZKNE1SMlIwS05xc08vTnFOZzF5WVNrTlZKTkRQUWpKeHpvMHdSSDJLdUhQVVhqTm9obCtHTk9kTGJ5dG43dVUrWGRPQVpsUHI2UUJxVnIwUC9IY0dqeVM3QUZucnFsNUlnL092R2JMWXB5VGE3eVVrOEpoNGpaNHczNE00cHl6OVpYVkJpdnVIekJ4eThubzQ1OGsvdnlsdFJLak50MGRMUW9MaUZuUW8wekJwbGROcytxWWZNUTJ1UXRFZjZpcURhOHJ6VkhBSWhYaXhNZEtYalZxZzdMSHdNdHk3LzY1NE91aGtFY2hEdWNnTG9uMmJmeHlCTnZ6VWNtcVY5NWVnZHBPM054UUFBQUFBPC94YWRlczpFbmNhcHN1bGF0ZWRUaW1lU3RhbXA+PC94YWRlczpTaWduYXR1cmVUaW1lU3RhbXA+PHhhZGVzOkNlcnRpZmljYXRlVmFsdWVzPjx4YWRlczpFbmNhcHN1bGF0ZWRYNTA5Q2VydGlmaWNhdGUgSWQ9ImlkLWE5ZmFlMDA0OTZhZTIwM2E2YThiOTJhZGJlNzYyYmQzLVJFU1BPTkRFUl9DRVJULTAiPk1JSUVpakNDQTNLZ0F3SUJBZ0lRYUk4eDZCbmFjWWROZE53bFlubi9tekFOQmdrcWhraUc5dzBCQVFVRkFEQjlNUXN3Q1FZRFZRUUdFd0pGUlRFaU1DQUdBMVVFQ2d3WlFWTWdVMlZ5ZEdsbWFYUnpaV1Z5YVcxcGMydGxjMnQxY3pFd01DNEdBMVVFQXd3blZFVlRWQ0J2WmlCRlJTQkRaWEowYVdacFkyRjBhVzl1SUVObGJuUnlaU0JTYjI5MElFTkJNUmd3RmdZSktvWklodmNOQVFrQkZnbHdhMmxBYzJzdVpXVXdIaGNOTVRFd016QTNNVE15TWpRMVdoY05NalF3T1RBM01USXlNalExV2pDQmd6RUxNQWtHQTFVRUJoTUNSVVV4SWpBZ0JnTlZCQW9NR1VGVElGTmxjblJwWm1sMGMyVmxjbWx0YVhOclpYTnJkWE14RFRBTEJnTlZCQXNNQkU5RFUxQXhKekFsQmdOVkJBTU1IbFJGVTFRZ2IyWWdVMHNnVDBOVFVDQlNSVk5RVDA1RVJWSWdNakF4TVRFWU1CWUdDU3FHU0liM0RRRUpBUllKY0d0cFFITnJMbVZsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEwY3c2Q2phMTdCYlliSGk2ZnJ3Y2NESTRCSVFMay9maUNFOEw0NW9zMHhoUGdFR1IrRUhFOExQQ0lxb2ZQZ2Y0Z3dOMXZERTZjUU5VbEswT2QrVXNoMzlpOVo0NWVzbmZwR3ErMkhzREphRm1GcjUrdUMxTUV6NUtuMVRhekV2S2JSamtHblNROUJlcnRsR2VyMkJsVS9rcU9rNXFBNVJ0SmZoVDBwc2MxaXhLZFBpcHY1OXduZituSHgxK1QrZlBXbmRYVlpMb0RnNHQzdzhsSXZJRS9LaE9TTWxFcnZCSUhJQUtWN3lIMWhPeHllR0xnaHF6TWlBbjNVZVRFT2dvT1M5VVJ2MEMvVDVDM21IK1kvdWFrTVN4ak51ejQxUG5laW1DemJFSlpKUmlFYU1JajhxUEF1YmNiTDhHdFkwM01XbWZOdFg2L3doNnU2VE1mVzhTMndJREFRQUJvNEgrTUlIN01CWUdBMVVkSlFFQi93UU1NQW9HQ0NzR0FRVUZCd01KTUIwR0ExVWREZ1FXQkJSOS81Q3VSb2tFZ0dpcVN6WXVaR1lBb2dsOFR6Q0JvQVlEVlIwZ0JJR1lNSUdWTUlHU0Jnb3JCZ0VFQWM0ZkF3RUJNSUdETUZnR0NDc0dBUVVGQndJQ01Fd2VTZ0JCQUdrQWJnQjFBR3dBZEFBZ0FIUUFaUUJ6QUhRQWFRQnRBR2tBY3dCbEFHc0Fjd0F1QUNBQVR3QnVBR3dBZVFBZ0FHWUFid0J5QUNBQWRBQmxBSE1BZEFCcEFHNEFad0F1TUNjR0NDc0dBUVVGQndJQkZodG9kSFJ3T2k4dmQzZDNMbk5yTG1WbEwyRnFZWFJsYlhCbGJDOHdId1lEVlIwakJCZ3dGb0FVdFRRS25hVXZFTVhuSVE2K3hMRmxSeHNEZHY0d0RRWUpLb1pJaHZjTkFRRUZCUUFEZ2dFQkFBYmFqN2tUcnVUQVBIcVRveWU5WnRCZGFKM0ZaamlLdWc5LzVSanNNd0RwT2VxRkRxQ29yTGQrREJJNHRnZHUwZzRsaGFJM2FWbktkUkJrR1YxOGtxcDg0dVU5N0pSRldRRWY2SDhocEo5ay9MekFBQ2tQM3REKzB5bSttZDUzMm1WK25SejFKaitSUExBVWs5eFlNVjdLUGN6Wk4xeG5sMndaREp3QmJRcGNTVkgxRGpsWnYzdEZMSEJMSVlUUzZxT0s0U3hTdGNnUnE3S2RSY3pmVzZtZlh6VENSV00zRzlubURlaTVRMytYVEVENDFqOHN6UldnbHpZZjZ6T3Y0ZGpramE2NFdZcmFRNXpiNHg4WGg3cVRDazZVdXBaN2plKzBvUmZ1ejBoLzN6eVJkamNSUGtqbG9TcFFwL05HOFJtcmNucjg3NHA4ZDlmZHdDclJJN1U9PC94YWRlczpFbmNhcHN1bGF0ZWRYNTA5Q2VydGlmaWNhdGU+PHhhZGVzOkVuY2Fwc3VsYXRlZFg1MDlDZXJ0aWZpY2F0ZT5NSUlGZkRDQ0JOMmdBd0lCQWdJUU5oanpTZmQyVUVwYmtPMTRFWTRPUlRBS0JnZ3Foa2pPUFFRREJEQmlNUXN3Q1FZRFZRUUdFd0pGUlRFYk1Ca0dBMVVFQ2d3U1Uwc2dTVVFnVTI5c2RYUnBiMjV6SUVGVE1SY3dGUVlEVlFSaERBNU9WRkpGUlMweE1EYzBOekF4TXpFZE1Cc0dBMVVFQXd3VVZFVlRWQ0J2WmlCRlJTMUhiM1pEUVRJd01UZ3dIaGNOTVRnd09UQTJNRGt3TXpVeVdoY05Nek13T0RNd01USTBPREk0V2pCZ01Rc3dDUVlEVlFRR0V3SkZSVEViTUJrR0ExVUVDZ3dTVTBzZ1NVUWdVMjlzZFhScGIyNXpJRUZUTVJjd0ZRWURWUVJoREE1T1ZGSkZSUzB4TURjME56QXhNekViTUJrR0ExVUVBd3dTVkVWVFZDQnZaaUJGVTFSRlNVUXlNREU0TUlHYk1CQUdCeXFHU000OUFnRUdCU3VCQkFBakE0R0dBQVFCeFl1ZzRjRXF3bUlqKzNUVmFVbGhmeENWOUZRZ2Z1Z2xDMi8wVXgxSWVxdzExbURqTnZuR0poa1d4YUxiV0ppN1F0dGhNRzVSMTA0bDdOcDdsQmV2ckJnQkR0ZmdqYTllM01MVFFrWStjRlMrVVF4anQ5WmloVFVKVnNSN2xvd1lsYUdFaXFxc0diRWhsd2Z1MjdYc204YjJyaFNpVE92TmRqVHRHNTdObndWQVgraWpnZ015TUlJRExqQWZCZ05WSFNNRUdEQVdnQlIvREhEWTlPV1BBWGZ1eDIwcEtibjB5Znhxd0RBZEJnTlZIUTRFRmdRVXdJU1pLY1JPbnpzQ05QYVo0UXBXQUFncFBuc3dEZ1lEVlIwUEFRSC9CQVFEQWdFR01CSUdBMVVkRXdFQi93UUlNQVlCQWY4Q0FRQXdnZ0hOQmdOVkhTQUVnZ0hFTUlJQndEQUlCZ1lFQUk5NkFRSXdDUVlIQkFDTDdFQUJBakF5QmdzckJnRUVBWU9SSVFFQ0FUQWpNQ0VHQ0NzR0FRVUZCd0lCRmhWb2RIUndjem92TDNkM2R5NXpheTVsWlM5RFVGTXdEUVlMS3dZQkJBR0RrU0VCQWdJd0RRWUxLd1lCQkFHRGtYOEJBZ0V3RFFZTEt3WUJCQUdEa1NFQkFnVXdEUVlMS3dZQkJBR0RrU0VCQWdZd0RRWUxLd1lCQkFHRGtTRUJBZ2N3RFFZTEt3WUJCQUdEa1NFQkFnTXdEUVlMS3dZQkJBR0RrU0VCQWdRd0RRWUxLd1lCQkFHRGtTRUJBZ2d3RFFZTEt3WUJCQUdEa1NFQkFna3dEUVlMS3dZQkJBR0RrU0VCQWdvd0RRWUxLd1lCQkFHRGtTRUJBZ3N3RFFZTEt3WUJCQUdEa1NFQkFnd3dEUVlMS3dZQkJBR0RrU0VCQWcwd0RRWUxLd1lCQkFHRGtTRUJBZzR3RFFZTEt3WUJCQUdEa1NFQkFnOHdEUVlMS3dZQkJBR0RrU0VCQWhBd0RRWUxLd1lCQkFHRGtTRUJBaEV3RFFZTEt3WUJCQUdEa1NFQkFoSXdEUVlMS3dZQkJBR0RrU0VCQWhNd0RRWUxLd1lCQkFHRGtTRUJBaFF3RFFZTEt3WUJCQUdEa1g4QkFnSXdEUVlMS3dZQkJBR0RrWDhCQWdNd0RRWUxLd1lCQkFHRGtYOEJBZ1F3RFFZTEt3WUJCQUdEa1g4QkFnVXdEUVlMS3dZQkJBR0RrWDhCQWdZd0tnWURWUjBsQVFIL0JDQXdIZ1lJS3dZQkJRVUhBd2tHQ0NzR0FRVUZCd01DQmdnckJnRUZCUWNEQkRCM0JnZ3JCZ0VGQlFjQkFRUnJNR2t3TGdZSUt3WUJCUVVITUFHR0ltaDBkSEE2THk5aGFXRXVaR1Z0Ynk1emF5NWxaUzlsWlMxbmIzWmpZVEl3TVRnd053WUlLd1lCQlFVSE1BS0dLMmgwZEhBNkx5OWpMbk5yTG1WbEwxUmxjM1JmYjJaZlJVVXRSMjkyUTBFeU1ERTRMbVJsY2k1amNuUXdHQVlJS3dZQkJRVUhBUU1FRERBS01BZ0dCZ1FBamtZQkFUQTRCZ05WSFI4RU1UQXZNQzJnSzZBcGhpZG9kSFJ3T2k4dll5NXpheTVsWlM5VVpYTjBYMjltWDBWRkxVZHZka05CTWpBeE9DNWpjbXd3Q2dZSUtvWkl6ajBFQXdRRGdZd0FNSUdJQWtJQklGK0xxeXR5YVY0bzV3VVNtMzBWeXNCOExkV3RvT3J6TnEyUWhCNnRHdjRzbGc1eitDUjU4ZTYwZVJGcU54VDdlY2NBL0hnb1BXczBCMVorTDA2N3F0VUNRZ0NCOE9QMGtIeC9qMXQ3aHROMkNYanBTakdGWnc1VFRJNHMxZUd5VGJlMFVKUkJYRWtVS2ZGYlpWbXpHUEZQcHJ3VWRTUGk4UHBPNyt4R0JZbEZIQTR6K1E9PTwveGFkZXM6RW5jYXBzdWxhdGVkWDUwOUNlcnRpZmljYXRlPjwveGFkZXM6Q2VydGlmaWNhdGVWYWx1ZXM+PHhhZGVzOlJldm9jYXRpb25WYWx1ZXM+PHhhZGVzOk9DU1BWYWx1ZXM+PHhhZGVzOkVuY2Fwc3VsYXRlZE9DU1BWYWx1ZT5NSUlHK3dvQkFLQ0NCdlF3Z2did0Jna3JCZ0VGQlFjd0FRRUVnZ2JoTUlJRzNUQ0NBUytoZ1lZd2dZTXhDekFKQmdOVkJBWVRBa1ZGTVNJd0lBWURWUVFLREJsQlV5QlRaWEowYVdacGRITmxaWEpwYldsemEyVnphM1Z6TVEwd0N3WURWUVFMREFSUFExTlFNU2N3SlFZRFZRUUREQjVVUlZOVUlHOW1JRk5MSUU5RFUxQWdVa1ZUVUU5T1JFVlNJREl3TVRFeEdEQVdCZ2txaGtpRzl3MEJDUUVXQ1hCcmFVQnpheTVsWlJnUE1qQXhPVEF5TWpJeE1UQTBNamRhTUdBd1hqQkpNQWtHQlNzT0F3SWFCUUFFRkRLWFFwS2NadlZYQzgvVEpJNVVpTmJScnF1ckJCVEFoSmtweEU2Zk93STA5cG5oQ2xZQUNDaytld0lRWVNNcDdDaHEyYnRieUdJNXVXMW5xSUFBR0E4eU1ERTVNREl5TWpFeE1EUXlOMXFoTVRBdk1DMEdDU3NHQVFVRkJ6QUJBZ1FnRVJPN0dGcEZqRXRvemlWd0tDNzMxQ0YvUWIrNGMyMW1EV1VLcDU1cFNROHdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSzltR09zbW5SNVhOaHZxYWxXM3crZjU3L2x1TFZtNi9UWnhBc1g0MDJJMVZSQTMwVUtmZ1JNTEJraFViMVpoM3AzM2hOc1l5cXFTUmNYdU1MU0VPU09jeHZBNzVNdFhQMkRrNTJXZDR0eFVyWGh3dE1wd0J0VzJWYzgrOGxEemZnQ1crbllKS0JxeWFNL2tMQlFYTE1ZdzRDOW0rak5iV2JOSVY4MU9MQUFsL3dKWEt4YXYrQndjeUxiNmRtSzU2ZlA3TGk4WEsyUk9mQzJRNkRnK05QcHBnb1ZaczdoUVBFbmJHbENtZ0V0amRIRElBMElSSXcvRGNDa3M1T1VNbWtLSFhpbGNNV0R5OVpobFJmSCtBNlk0NS9oOHpPN0ZPa3JYQmM3RW9BS1ptS3pNNCtDZGpFRHRObkFNTGU0TmJ5OGl1NVNjR0RVZWFvbE11NU5ZTnV5Z2dnU1NNSUlFampDQ0JJb3dnZ055b0FNQ0FRSUNFR2lQTWVnWjJuR0hUWFRjSldKNS81c3dEUVlKS29aSWh2Y05BUUVGQlFBd2ZURUxNQWtHQTFVRUJoTUNSVVV4SWpBZ0JnTlZCQW9NR1VGVElGTmxjblJwWm1sMGMyVmxjbWx0YVhOclpYTnJkWE14TURBdUJnTlZCQU1NSjFSRlUxUWdiMllnUlVVZ1EyVnlkR2xtYVdOaGRHbHZiaUJEWlc1MGNtVWdVbTl2ZENCRFFURVlNQllHQ1NxR1NJYjNEUUVKQVJZSmNHdHBRSE5yTG1WbE1CNFhEVEV4TURNd056RXpNakkwTlZvWERUSTBNRGt3TnpFeU1qSTBOVm93Z1lNeEN6QUpCZ05WQkFZVEFrVkZNU0l3SUFZRFZRUUtEQmxCVXlCVFpYSjBhV1pwZEhObFpYSnBiV2x6YTJWemEzVnpNUTB3Q3dZRFZRUUxEQVJQUTFOUU1TY3dKUVlEVlFRRERCNVVSVk5VSUc5bUlGTkxJRTlEVTFBZ1VrVlRVRTlPUkVWU0lESXdNVEV4R0RBV0Jna3Foa2lHOXcwQkNRRVdDWEJyYVVCemF5NWxaVENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFOSE1PZ28ydGV3VzJHeDR1bjY4SEhBeU9BU0VDNVAzNGdoUEMrT2FMTk1ZVDRCQmtmaEJ4UEN6d2lLcUh6NEgrSU1EZGJ3eE9uRURWSlN0RG5mbExJZC9ZdldlT1hySjM2UnF2dGg3QXlXaFpoYStmcmd0VEJNK1NwOVUyc3hMeW0wWTVCcDBrUFFYcTdaUm5xOWdaVlA1S2pwT2FnT1ViU1g0VTlLYkhOWXNTblQ0cWIrZmNKMy9weDhkZmsvbnoxcDNWMVdTNkE0T0xkOFBKU0x5QlB5b1RrakpSSzd3U0J5QUNsZThoOVlUc2NuaGk0SWFzeklnSjkxSGt4RG9LRGt2VkViOUF2MCtRdDVoL21QN21wREVzWXpicytOVDUzb3BnczJ4Q1dTVVloR2pDSS9LandMbTNHeS9CcldOTnpGcG56YlYrdjhJZXJ1a3pIMXZFdHNDQXdFQUFhT0IvakNCK3pBV0JnTlZIU1VCQWY4RUREQUtCZ2dyQmdFRkJRY0RDVEFkQmdOVkhRNEVGZ1FVZmYrUXJrYUpCSUJvcWtzMkxtUm1BS0lKZkU4d2dhQUdBMVVkSUFTQm1EQ0JsVENCa2dZS0t3WUJCQUhPSHdNQkFUQ0JnekJZQmdnckJnRUZCUWNDQWpCTUhrb0FRUUJwQUc0QWRRQnNBSFFBSUFCMEFHVUFjd0IwQUdrQWJRQnBBSE1BWlFCckFITUFMZ0FnQUU4QWJnQnNBSGtBSUFCbUFHOEFjZ0FnQUhRQVpRQnpBSFFBYVFCdUFHY0FMakFuQmdnckJnRUZCUWNDQVJZYmFIUjBjRG92TDNkM2R5NXpheTVsWlM5aGFtRjBaVzF3Wld3dk1COEdBMVVkSXdRWU1CYUFGTFUwQ3AybEx4REY1eUVPdnNTeFpVY2JBM2IrTUEwR0NTcUdTSWIzRFFFQkJRVUFBNElCQVFBRzJvKzVFNjdrd0R4Nms2TW52V2JRWFdpZHhXWTRpcm9QZitVWTdETUE2VG5xaFE2Z3FLeTNmZ3dTT0xZSGJ0SU9KWVdpTjJsWnluVVFaQmxkZkpLcWZPTGxQZXlVUlZrQkgraC9JYVNmWlB5OHdBQXBEOTdRL3RNcHZwbmVkOXBsZnAwYzlTWS9rVHl3RkpQY1dERmV5ajNNMlRkY1o1ZHNHUXljQVcwS1hFbFI5UTQ1V2I5N1JTeHdTeUdFMHVxaml1RXNVclhJRWF1eW5VWE0zMXVwbjE4MHdrVmpOeHZaNWczb3VVTi9sMHhBK05ZL0xNMFZvSmMySCtzenIrSFk1STJ1dUZtSzJrT2MyK01mRjRlNmt3cE9sTHFXZTQzdnRLRVg3czlJZjk4OGtYWTNFVDVJNWFFcVVLZnpSdkVacTNKNi9PK0tmSGZYM2NBcTBTTzE8L3hhZGVzOkVuY2Fwc3VsYXRlZE9DU1BWYWx1ZT48L3hhZGVzOk9DU1BWYWx1ZXM+PC94YWRlczpSZXZvY2F0aW9uVmFsdWVzPjwveGFkZXM6VW5zaWduZWRTaWduYXR1cmVQcm9wZXJ0aWVzPjwveGFkZXM6VW5zaWduZWRQcm9wZXJ0aWVzPjwveGFkZXM6UXVhbGlmeWluZ1Byb3BlcnRpZXM+PC9kczpPYmplY3Q+PC9kczpTaWduYXR1cmU+PC9hc2ljOlhBZEVTU2lnbmF0dXJlcz5QSwECCgAKAAAIAAAjYXpOiiH5RR8AAAAfAAAACAAAAAAAAAAAAAAAAAAAAAAAbWltZXR5cGVQSwECFAAUAAgICAAjYXpONp+/XbQAAAC7AQAAFQAAAAAAAAAAAAAAAABFAAAATUVUQS1JTkYvbWFuaWZlc3QueG1sUEsBAhQAFAAICAgAI2F6TphH5srFAAAAAwEAAB0AAAAAAAAAAAAAAAAAPAEAAE1FVEEtSU5GL2hhc2hjb2Rlcy1zaGEyNTYueG1sUEsBAhQAFAAICAgAI2F6TiG3/O8JAQAAWwEAAB0AAAAAAAAAAAAAAAAATAIAAE1FVEEtSU5GL2hhc2hjb2Rlcy1zaGE1MTIueG1sUEsBAgoACgAACAAAI2F6Thg2QZdXNAAAVzQAABgAAAAAAAAAAAAAAAAAoAMAAE1FVEEtSU5GL3NpZ25hdHVyZXMwLnhtbFBLBQYAAAAABQAFAFUBAAAtOAAAAAA="

    static Map defaultFile() {
        [fileName      : "testing.txt",
         fileHashSha256: "RnKZobNWVy8u92sDL4S2j1BUzMT5qTgt6hm90TfAGRo=",
         fileHashSha512: "hQVz9wirVZNvP/q3HoaW8nu0FfvrGkZinhADKE4Y4j/dUuGfgONfR4VYdu0p/dj/yGH0qlE0FGsmUB2N3oLuhA==",
         fileSize      : 189]
    }

    // Error codes
    static final String INVALID_REQUEST = "REQUEST_VALIDATION_EXCEPTION"

    // MID error codes
    static final String SENDING_ERROR = "SENDING_ERROR"
    static final String USER_CANCEL = "USER_CANCEL"
    static final String NOT_VALID = "NOT_VALID"
    static final String PHONE_ABSENT = "PHONE_ABSENT"
    static final String EXPIRED_TRANSACTION = "EXPIRED_TRANSACTION"

    static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND_EXCEPTION"
    static final String CLIENT_EXCEPTION = "CLIENT_EXCEPTION"
    static final String MID_EXCEPTION = "MID_EXCEPTION"
    static final String NOT_FOUND = "NOT_FOUND"
    static final String SIM_ERROR = "SIM_ERROR"
    static final String INVALID_CONTAINER = "INVALID_CONTAINER_EXCEPTION"
    static final String AUTHORIZATION_ERROR = "AUTHORIZATION_ERROR"
    static final String INVALID_SIGNATURE = "SIGNATURE_CREATION_EXCEPTION"
    static final String INVALID_LANGUAGE = "INVALID_LANGUAGE_EXCEPTION"
    static final String INVALID_DATA = "INVALID_SESSION_DATA_EXCEPTION"
    static final String DUPLICATE_DATA_FILE = "DUPLICATE_DATA_FILE_EXCEPTION"
    static final String INVALID_CERTIFICATE_EXCEPTION = "INVALID_CERTIFICATE_EXCEPTION"
    static final String INVALID_SIGNATURE_EXCEPTION = "INVALID_SIGNATURE_EXCEPTION"
    static final String SMARTID_EXCEPTION = "SMARTID_EXCEPTION"
    static final String USER_SELECTED_WRONG_VC = "USER_SELECTED_WRONG_VC"
    static final String CONNECTION_LIMIT_EXCEPTION = "CONNECTION_LIMIT_EXCEPTION"
    static final String REQUEST_SIZE_LIMIT_EXCEPTION = "REQUEST_SIZE_LIMIT_EXCEPTION"
    static final String INVALID_CONTAINER_EXCEPTION = "INVALID_CONTAINER_EXCEPTION"
    static final String INVALID_SESSION_DATA_EXCEPTION = "INVALID_SESSION_DATA_EXCEPTION"
    




}
